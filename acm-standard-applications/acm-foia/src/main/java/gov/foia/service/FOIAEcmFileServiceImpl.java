package gov.foia.service;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.*;
import com.armedia.acm.plugins.ecm.service.impl.EcmFileServiceImpl;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.service.objectlock.annotation.AcmAcquireAndReleaseObjectLock;
import gov.foia.model.FOIAEcmFileVersion;
import org.apache.chemistry.opencmis.client.api.Document;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.PersistenceException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FOIAEcmFileServiceImpl extends EcmFileServiceImpl implements FOIAEcmFileService
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    @AcmAcquireAndReleaseObjectLock(objectIdArgIndex = 0, objectType = "FILE", lockType = "READ")
    @AcmAcquireAndReleaseObjectLock(acmObjectArgIndex = 1, objectType = "FOLDER", lockType = "WRITE", lockChildObjects = false, unlockChildObjects = false)
    public EcmFile copyFile(Long fileId, AcmFolder targetFolder, AcmContainer targetContainer)
            throws AcmUserActionFailedException, AcmObjectNotFoundException
    {
        EcmFile file = getEcmFileDao().find(fileId);

        if (file == null || targetFolder == null)
        {
            throw new AcmObjectNotFoundException(EcmFileConstants.OBJECT_FILE_TYPE, fileId, "File or Destination folder not found", null);
        }
        String internalFileName = getFolderAndFilesUtils().createUniqueIdentificator(file.getFileName());
        Map<String, Object> props = new HashMap<>();
        props.put(EcmFileConstants.ECM_FILE_ID, getFolderAndFilesUtils().getActiveVersionCmisId(file));
        props.put(EcmFileConstants.DST_FOLDER_ID, targetFolder.getCmisFolderId());
        props.put(EcmFileConstants.FILE_NAME, internalFileName);
        props.put(EcmFileConstants.FILE_MIME_TYPE, file.getFileActiveVersionMimeType());
        String cmisRepositoryId = targetFolder.getCmisRepositoryId();
        if (cmisRepositoryId == null)
        {
            cmisRepositoryId = getEcmFileServiceProperties().getProperty("ecm.defaultCmisId");
        }
        props.put(EcmFileConstants.CONFIGURATION_REFERENCE, getCmisConfigUtils().getCmisConfiguration(cmisRepositoryId));
        props.put(EcmFileConstants.VERSIONING_STATE, getCmisConfigUtils().getVersioningState(cmisRepositoryId));
        EcmFile result;

        try
        {
            MuleMessage message = getMuleContextManager().send(EcmFileConstants.MULE_ENDPOINT_COPY_FILE, file, props);

            if (message.getInboundPropertyNames().contains(EcmFileConstants.COPY_FILE_EXCEPTION_INBOUND_PROPERTY))
            {
                MuleException muleException = message.getInboundProperty(EcmFileConstants.COPY_FILE_EXCEPTION_INBOUND_PROPERTY);
                log.error("File can not be copied successfully {} ", muleException.getMessage(), muleException);
                throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_COPY_FILE, EcmFileConstants.OBJECT_FILE_TYPE, fileId,
                        "File " + file.getFileName() + " can not be copied successfully", muleException);
            }

            Document cmisObject = message.getPayload(Document.class);

            EcmFile fileCopy = new EcmFile();

            fileCopy.setVersionSeriesId(cmisObject.getVersionSeriesId());
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

            FOIAEcmFileVersion fileCopyVersion = new FOIAEcmFileVersion();
            fileCopyVersion.setVersionMimeType(file.getFileActiveVersionMimeType());
            fileCopyVersion.setVersionFileNameExtension(file.getFileActiveVersionNameExtension());
            fileCopyVersion.setCmisObjectId(cmisObject.getId());
            fileCopyVersion.setFile(file);
            fileCopyVersion.setVersionTag(cmisObject.getVersionLabel());
            fileCopyVersion.setReviewStatus(new String());
            fileCopyVersion.setRedactionStatus(new String());

            ObjectAssociation personCopy = copyObjectAssociation(file.getPersonAssociation());
            fileCopy.setPersonAssociation(personCopy);

            ObjectAssociation organizationCopy = copyObjectAssociation(file.getOrganizationAssociation());
            fileCopy.setOrganizationAssociation(organizationCopy);

            copyFileVersionMetadata(file, fileCopyVersion);

            fileCopy.getVersions().add(fileCopyVersion);

            result = getEcmFileDao().save(fileCopy);

            result = getFileParticipantService().setFileParticipantsFromParentFolder(result);

            return result;
        }
        catch (MuleException e)
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
    public void setReviewStatus(Long fileId, String fileVersion, String reviewStatus)
    {
        setReviewRedactionStatus(fileId, fileVersion, reviewStatus, "review");
    }

    @Override
    public void setRedactionStatus(Long fileId, String fileVersion, String redactionStatus)
    {
        setReviewRedactionStatus(fileId, fileVersion, redactionStatus, "redaction");
    }

    private void setReviewRedactionStatus(Long fileId, String fileVersion, String status, String statusType)
    {
        EcmFile file = getEcmFileDao().find(fileId);
        List<EcmFileVersion> fileVersions = file.getVersions();

        EcmFileVersion fileVersionToUpdate = fileVersions
                .stream()
                .filter(ecmFileVersion -> ecmFileVersion.getVersionTag().equals(fileVersion))
                .findFirst()
                .orElse(null);

        if(Objects.nonNull(fileVersionToUpdate) && fileVersionToUpdate instanceof FOIAEcmFileVersion)
        {
            if("review".equals(statusType))
            {
                ((FOIAEcmFileVersion)fileVersionToUpdate).setReviewStatus(status);
            }
            else if("redaction".equals(statusType))
            {
                ((FOIAEcmFileVersion)fileVersionToUpdate).setRedactionStatus(status);
            }
            getEcmFileDao().save(file);
        }
    }
}
