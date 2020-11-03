package gov.privacy.service;

/*-
 * #%L
 * ACM Privacy: Subject Access Request
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.camelcontext.arkcase.cmis.ArkCaseCMISActions;
import com.armedia.acm.camelcontext.arkcase.cmis.ArkCaseCMISConstants;
import com.armedia.acm.camelcontext.exception.ArkCaseFileRepositoryException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.exception.LinkAlreadyExistException;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.plugins.ecm.model.event.EcmFileConvertEvent;
import com.armedia.acm.plugins.ecm.service.impl.EcmFileServiceImpl;
import com.armedia.acm.plugins.ecm.utils.EcmFileCamelUtils;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.service.objectlock.annotation.AcmAcquireAndReleaseObjectLock;
import com.armedia.acm.web.api.MDCConstants;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.PersistenceException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import gov.privacy.dao.SARFileDao;
import gov.privacy.model.SAREcmFileVersion;
import gov.privacy.model.SARFile;

/**
 * @author Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Jun, 2020
 *
 */
public class SAREcmFileServiceImpl extends EcmFileServiceImpl implements SAREcmFileService
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private SARFileDao SARFileDao;

    @Override
    @AcmAcquireAndReleaseObjectLock(objectIdArgIndex = 0, objectType = "FILE", lockType = "READ")
    @AcmAcquireAndReleaseObjectLock(acmObjectArgIndex = 1, objectType = "FOLDER", lockType = "WRITE", lockChildObjects = false, unlockChildObjects = false)
    public EcmFile copyFile(Long fileId, AcmFolder targetFolder, AcmContainer targetContainer)
            throws AcmUserActionFailedException, AcmObjectNotFoundException
    {
        SARFile file = getSARFileDao().find(fileId);

        if (file == null || targetFolder == null)
        {
            throw new AcmObjectNotFoundException(EcmFileConstants.OBJECT_FILE_TYPE, fileId, "File or Destination folder not found", null);
        }
        String internalFileName = getFolderAndFilesUtils().createUniqueIdentificator(file.getFileName());
        Map<String, Object> props = new HashMap<>();
        props.put(EcmFileConstants.CMIS_DOCUMENT_ID, getFolderAndFilesUtils().getActiveVersionCmisId(file));
        props.put(EcmFileConstants.DST_FOLDER_ID, targetFolder.getCmisFolderId());
        props.put(PropertyIds.NAME, internalFileName);
        props.put(EcmFileConstants.FILE_MIME_TYPE, file.getFileActiveVersionMimeType());
        String cmisRepositoryId = targetFolder.getCmisRepositoryId();
        if (cmisRepositoryId == null)
        {
            cmisRepositoryId = getEcmFileConfig().getDefaultCmisId();
        }
        props.put(EcmFileConstants.CMIS_REPOSITORY_ID, ArkCaseCMISConstants.CAMEL_CMIS_DEFAULT_REPO_ID);
        props.put(EcmFileConstants.VERSIONING_STATE,
                getCmisConfigUtils().getVersioningState(ArkCaseCMISConstants.CAMEL_CMIS_DEFAULT_REPO_ID));
        props.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, EcmFileCamelUtils.getCmisUser());

        try
        {
            Document cmisObject = (Document) getCamelContextManager().send(ArkCaseCMISActions.COPY_DOCUMENT, props);

            SARFile fileCopy = new SARFile();

            fileCopy.setVersionSeriesId(cmisObject.getPropertyValue(EcmFileConstants.REPOSITORY_VERSION_ID));
            fileCopy.setFileType(file.getFileType());
            fileCopy.setActiveVersionTag(cmisObject.getVersionLabel());
            fileCopy.setFileName(file.getFileName());
            fileCopy.setFolder(targetFolder);
            fileCopy.setContainer(targetContainer);
            fileCopy.setStatus(file.getStatus());
            fileCopy.setCategory(file.getCategory());
            fileCopy.setFileActiveVersionMimeType(file.getFileActiveVersionMimeType());
            fileCopy.setClassName(file.getClassName());
            fileCopy.setFileActiveVersionNameExtension(file.getFileActiveVersionNameExtension());
            fileCopy.setFileSource(file.getFileSource());
            fileCopy.setLegacySystemId(file.getLegacySystemId());
            fileCopy.setPageCount(file.getPageCount());
            fileCopy.setSecurityField(file.getSecurityField());

            //if someone is refactoring this using copyEcmFile method don't forget this line
            fileCopy.setDuplicate(file.isDuplicate());

            fileCopy.setPublicFlag(file.getPublicFlag());

            SAREcmFileVersion fileCopyVersion = new SAREcmFileVersion();
            fileCopyVersion.setCmisObjectId(
                    cmisObject.getPropertyValue(EcmFileConstants.REPOSITORY_VERSION_ID) + ";" + cmisObject.getVersionLabel());
            fileCopyVersion.setVersionTag(cmisObject.getVersionLabel());
            fileCopyVersion.setReviewStatus(new String());
            fileCopyVersion.setRedactionStatus(new String());

            ObjectAssociation personCopy = copyObjectAssociation(file.getPersonAssociation());
            fileCopy.setPersonAssociation(personCopy);

            ObjectAssociation organizationCopy = copyObjectAssociation(file.getOrganizationAssociation());
            fileCopy.setOrganizationAssociation(organizationCopy);

            copyFileVersionMetadata(file, fileCopyVersion);

            fileCopy.getVersions().add(fileCopyVersion);

            SARFile result = getSARFileDao().save(fileCopy);

            getFileEventPublisher().publishFileCopiedEvent(fileCopy, file, SecurityContextHolder.getContext().getAuthentication(), null, true);

            return getFileParticipantService().setFileParticipantsFromParentFolder(result);
        }
        catch (ArkCaseFileRepositoryException e)
        {
            log.error("Could not copy file {} ", e.getMessage(), e);
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_COPY_FILE, EcmFileConstants.OBJECT_FILE_TYPE, file.getId(),
                    "Could not copy file", e);
        }
        catch (PersistenceException e)
        {
            log.error("Could not copy file {} ", e.getMessage(), e);
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_COPY_FILE, EcmFileConstants.OBJECT_FILE_TYPE, file.getId(),
                    "Could not copy file", e);
        }
    }

    @Override
    @AcmAcquireAndReleaseObjectLock(objectIdArgIndex = 0, objectType = "FILE", lockType = "READ")
    @AcmAcquireAndReleaseObjectLock(acmObjectArgIndex = 1, objectType = "FOLDER", lockType = "WRITE", lockChildObjects = false, unlockChildObjects = false)
    public EcmFile copyFileAsLink(Long fileId, AcmFolder targetFolder, AcmContainer targetContainer)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, LinkAlreadyExistException
    {
        SARFile file = getSARFileDao().find(fileId);

        if (file == null || targetFolder == null)
        {
            throw new AcmObjectNotFoundException(EcmFileConstants.OBJECT_FILE_TYPE, fileId, "File or Destination folder not found", null);
        }

        // Check if link already exists in same directory
        if (getEcmFileDao().getFileLinkInCurrentDirectory(file.getVersionSeriesId(), targetFolder.getId()) != null)
        {
            log.error("File with version series id {} already exist in current directory", file.getVersionSeriesId());
            throw new LinkAlreadyExistException("Link for file " + file.getFileName() + " already exist " +
                    "in current directory");
        }

        SARFile savedFile;

        try
        {

            EcmFileVersion fileCopyVersion = new SAREcmFileVersion();
            fileCopyVersion.setCmisObjectId(file.getVersions().get(file.getVersions().size() - 1).getCmisObjectId());
            fileCopyVersion.setVersionTag(file.getActiveVersionTag());
            copyFileVersionMetadata(file, fileCopyVersion);

            SARFile fileCopy = copyEcmFile(file, targetFolder, targetContainer, fileCopyVersion, file.getVersionSeriesId(),
                    file.getActiveVersionTag());
            fileCopy.setLink(true);

            savedFile = getSARFileDao().save(fileCopy);

            return getFileParticipantService().setFileParticipantsFromParentFolder(savedFile);

        }
        catch (PersistenceException e)
        {
            log.error("Could not create link to file {} ", e.getMessage(), e);
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_COPY_FILE, EcmFileConstants.OBJECT_FILE_TYPE, file.getId(),
                    "Could not create link to file", e);
        }
    }

    protected SARFile copyEcmFile(SARFile originalFile, AcmFolder targetFolder, AcmContainer targetContainer,
                                  EcmFileVersion fileVersion, String versionSeriesId, String activeVersionTag)
    {
        SARFile fileCopy = new SARFile();

        fileCopy.setVersionSeriesId(versionSeriesId);
        fileCopy.setFileType(originalFile.getFileType());
        fileCopy.setActiveVersionTag(activeVersionTag);
        fileCopy.setFileName(originalFile.getFileName());
        fileCopy.setFolder(targetFolder);
        fileCopy.setContainer(targetContainer);
        fileCopy.setStatus(originalFile.getStatus());
        fileCopy.setCategory(originalFile.getCategory());
        fileCopy.setFileActiveVersionMimeType(originalFile.getFileActiveVersionMimeType());
        fileCopy.setClassName(originalFile.getClassName());
        fileCopy.setFileActiveVersionNameExtension(originalFile.getFileActiveVersionNameExtension());
        fileCopy.setFileSource(originalFile.getFileSource());
        fileCopy.setLegacySystemId(originalFile.getLegacySystemId());
        fileCopy.setPageCount(originalFile.getPageCount());
        fileCopy.setSecurityField(originalFile.getSecurityField());

        fileCopy.setPublicFlag(originalFile.getPublicFlag());

        ObjectAssociation personCopy = copyObjectAssociation(originalFile.getPersonAssociation());
        fileCopy.setPersonAssociation(personCopy);

        ObjectAssociation organizationCopy = copyObjectAssociation(originalFile.getOrganizationAssociation());
        fileCopy.setOrganizationAssociation(organizationCopy);

        fileCopy.getVersions().add(fileVersion);
        return fileCopy;
    }

    @Override
    public void setReviewStatus(Long fileId, String fileVersion, String reviewStatus) throws AcmObjectNotFoundException
    {
        setReviewRedactionStatus(fileId, fileVersion, reviewStatus, "review");
    }

    @Override
    public void setRedactionStatus(Long fileId, String fileVersion, String redactionStatus) throws AcmObjectNotFoundException
    {
        setReviewRedactionStatus(fileId, fileVersion, redactionStatus, "redaction");
    }

    private void setReviewRedactionStatus(Long fileId, String fileVersion, String status, String statusType)
            throws AcmObjectNotFoundException
    {
        EcmFile file = getEcmFileDao().find(fileId);
        List<EcmFileVersion> fileVersions = file.getVersions();

        EcmFileVersion fileVersionToUpdate = fileVersions
                .stream()
                .filter(ecmFileVersion -> ecmFileVersion.getVersionTag().equals(fileVersion))
                .findFirst()
                .orElse(null);

        if (Objects.nonNull(fileVersionToUpdate) && fileVersionToUpdate instanceof SAREcmFileVersion)
        {
            if ("review".equals(statusType))
            {
                ((SAREcmFileVersion) fileVersionToUpdate).setReviewStatus(status);
            }
            else if ("redaction".equals(statusType))
            {
                ((SAREcmFileVersion) fileVersionToUpdate).setRedactionStatus(status);
            }
            updateFileLinks(file);
            file.setModified(new Date());
            getEcmFileDao().save(file);
        }
    }

    @Override
    public File convertFile(String fileKey, String version, String fileExtension, String fileName, String mimeType, EcmFile ecmFile)
            throws IOException
    {
        InputStream fileIs;
        InputStream pdfConvertedIs;
        File tmpPdfConvertedFile = null;

        String timestamp = String.valueOf(new Timestamp(System.currentTimeMillis()).getTime());
        String tmpPdfConvertedFileName = timestamp.concat(fileKey).concat(".pdf");
        String tmpPdfConvertedFullFileName = FileUtils.getTempDirectoryPath().concat(File.separator).concat(tmpPdfConvertedFileName);

        Map<String, Object> eventProperties = new HashMap<>();
        eventProperties.put("ecmFileVersion", version);
        eventProperties.put("tmpPdfConvertedFullFileName", tmpPdfConvertedFullFileName);

        // An Event listener is performing the conversion
        EcmFileConvertEvent ecmFileConvertEvent = new EcmFileConvertEvent(ecmFile, eventProperties);
        getApplicationEventPublisher().publishEvent(ecmFileConvertEvent);
        //

        tmpPdfConvertedFile = new File(tmpPdfConvertedFullFileName);

        return tmpPdfConvertedFile;
    }

    @Override
    public void updateFileLinks(EcmFile file) throws AcmObjectNotFoundException
    {
        List<EcmFile> links = getFileLinks(file.getFileId());
        SAREcmFileVersion activeFileVersion = file.getVersions()
                .stream()
                .filter(ecmFileVersion -> ecmFileVersion.getVersionTag().equals(file.getActiveVersionTag()))
                .map(it -> (SAREcmFileVersion) it)
                .findFirst().orElse(null);

        links.forEach(f -> {
            f.setFileType(file.getFileType());
            f.setActiveVersionTag(file.getActiveVersionTag());
            f.setFileName(file.getFileName());
            f.setContainer(file.getContainer());
            f.setStatus(file.getStatus());
            f.setCategory(file.getCategory());
            f.setFileActiveVersionMimeType(file.getFileActiveVersionMimeType());
            f.setClassName(file.getClassName());
            f.setFileActiveVersionNameExtension(file.getFileActiveVersionNameExtension());
            f.setFileSource(file.getFileSource());
            f.setLegacySystemId(file.getLegacySystemId());
            f.setPageCount(file.getPageCount());
            f.setSecurityField(file.getSecurityField());

            SAREcmFileVersion linkVersion = (SAREcmFileVersion) f.getVersions().get(0);
            linkVersion.setVersionTag(file.getActiveVersionTag());
            if (Objects.nonNull(activeFileVersion))
            {
                linkVersion.setCmisObjectId(activeFileVersion.getCmisObjectId());
                linkVersion.setRedactionStatus(activeFileVersion.getRedactionStatus());
                linkVersion.setReviewStatus(activeFileVersion.getReviewStatus());
            }
            f.getVersions().set(0, linkVersion);

            getEcmFileDao().save(f);
        });
    }

    /**
     * @return the SARFileDao
     */
    public SARFileDao getSARFileDao()
    {
        return SARFileDao;
    }

    /**
     * @param SARFileDao
     *            the SARFileDao to set
     */
    public void setSARFileDao(SARFileDao SARFileDao)
    {
        this.SARFileDao = SARFileDao;
    }

}