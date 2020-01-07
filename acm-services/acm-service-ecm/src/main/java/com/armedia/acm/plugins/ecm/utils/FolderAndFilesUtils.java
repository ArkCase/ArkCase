package com.armedia.acm.plugins.ecm.utils;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
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

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.plugins.ecm.model.sync.EcmEvent;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.persistence.NoResultException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by marjan.stefanoski on 09.04.2015.
 */
public class FolderAndFilesUtils
{

    private Logger log = LogManager.getLogger(getClass());
    private AcmFolderDao folderDao;
    private EcmFileDao fileDao;
    private AcmFolderService folderService;
    private EcmFileService fileService;

    /**
     * Replace all not allowed characters in folder name with underscore
     *
     * @param folderName
     * @return
     */
    public String buildSafeFolderName(String folderName)
    {
        if (folderName != null)
        {
            String regex = EcmFileConstants.INVALID_CHARACTERS_IN_FOLDER_NAME_REGEX;
            String replacement = EcmFileConstants.INVALID_CHARACTERS_IN_FOLDER_NAME_REPLACEMENT;

            folderName = folderName.replaceAll(regex, replacement);
        }
        return folderName;
    }

    public String getActiveVersionCmisId(EcmFile ecmFile)
    {
        List<EcmFileVersion> versions = ecmFile.getVersions();
        if (versions == null)
        {
            return ecmFile.getVersionSeriesId();
        }
        String cmisId = null;

        // follow this way for now till we figure out
        // why stream code below is not working
        for (EcmFileVersion version : versions)
        {
            if (version.getVersionTag().equals(ecmFile.getActiveVersionTag()))
            {
                cmisId = version.getCmisObjectId();
            }
        }
        if (cmisId == null)
        {
            cmisId = ecmFile.getVersionSeriesId();
        }
        /*
         * cmisId = versions.stream().filter(fv -> (fv.getVersionTag()).equals(ecmFile.getActiveVersionTag())).
         * map(EcmFileVersion::getCmisObjectId).findFirst().orElse(ecmFile.getVersionSeriesId());
         */

        return cmisId;
    }

    public String getVersionCmisId(EcmFile ecmFile, String version)
    {

        if (StringUtils.isEmpty(version))
        {
            return getActiveVersionCmisId(ecmFile);
        }

        EcmFileVersion ecmFileVersion = getVersion(ecmFile, version);

        return ecmFileVersion != null ? ecmFileVersion.getCmisObjectId() : getActiveVersionCmisId(ecmFile);

    }

    public EcmFileVersion getVersion(EcmFile ecmFile, String version)
    {
        if (ecmFile != null && ecmFile.getVersions() != null && version != null)
        {
            return ecmFile.getVersions().stream().filter(fv -> version.equals(fv.getVersionTag())).findFirst().orElse(null);
        }

        return null;
    }

    public String createUniqueIdentificator(String input)
    {
        if (input != null && input.length() > 0)
        {
            input = input.replace(" ", "_");

            SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmmssSSS");
            String dateString = dateFormat.format(new Date());

            String[] inputArray = input.split("\\.");

            if (inputArray != null && inputArray.length == 1)
            {
                input = input + "_" + dateString;
            }
            else if (inputArray != null && inputArray.length > 1)
            {
                input = input.replace("." + inputArray[inputArray.length - 1], "_" + dateString + "." + inputArray[inputArray.length - 1]);
            }
        }

        return input;
    }

    public String createUniqueFolderName(String name)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmmssSSS");
        String dateString = dateFormat.format(new Date());
        return name + "_" + dateString;
    }

    public Long convertToLong(String folderId)
    {
        try
        {
            return Long.parseLong(folderId);
        }
        catch (Exception e)
        {
            log.error("Cannot convert String representation of folderId=" + folderId + " to Long", e);
        }

        return null;
    }

    /**
     * Returns a PDF file which matches the supplied ArkCase model file type from the list and which is a PDF document
     * since only PDF files can be merged
     *
     * @param fileList
     *            - List of ecmFiles which will be searched for the desired type
     * @param fileType
     *            - type to search for in the ecm file list
     * @return ecmFile which has the given ArkCase type and is a PDF, or null if not found
     */
    public EcmFile findMatchingPDFFileType(List<EcmFile> fileList, String fileType)
    {
        EcmFile matchFile = null;
        for (EcmFile ecmFile : fileList)
        {
            if (ecmFile.getFileType().equalsIgnoreCase(fileType) && ecmFile.getFileActiveVersionMimeType().equals("application/pdf"))
            {
                matchFile = ecmFile;
            }
        }
        return matchFile;
    }

    public String getBaseFileName(String fileName)
    {
        if (fileName.lastIndexOf(".") > 0)
        {
            return fileName.substring(0, fileName.lastIndexOf("."));
        }
        else
        {
            return fileName;
        }
    }

    public String getBaseFileName(String fileName, String fileExtension)
    {
        // endsWith throws NPE on null input
        if (fileName.lastIndexOf(".") > 0 && fileExtension != null && StringUtils.endsWithIgnoreCase(fileName, fileExtension))
        {
            return fileName.substring(0, fileName.lastIndexOf("."));
        }
        else
        {
            return fileName;
        }
    }

    public String getFileNameExtension(String fileName)
    {
        if (fileName.lastIndexOf(".") > 0)
        {
            return fileName.substring(fileName.lastIndexOf("."));
        }
        else
        {
            return "";
        }
    }

    public EcmFile uploadFile(EcmEvent ecmEvent, AcmFolder targetParentFolder)
            throws AcmUserActionFailedException, AcmCreateObjectFailedException
    {
        return uploadFile(ecmEvent.getNodeId(), ecmEvent.getNodeName(), ecmEvent.getUserId(), targetParentFolder);
    }

    public EcmFile uploadFile(String nodeId, String nodeName, String userId, AcmFolder targetParentFolder)
            throws AcmCreateObjectFailedException, AcmUserActionFailedException
    {
        AcmContainer container = lookupArkCaseContainer(targetParentFolder.getId());
        if (container == null)
        {
            log.debug("Can't find container for the new file with id {}, exiting.", nodeId);
            return null;
        }

        String cmisRepositoryId = getFolderService().getCmisRepositoryId(targetParentFolder);
        Document cmisDocument = lookupCmisDocument(cmisRepositoryId, nodeId);
        if (cmisDocument == null)
        {
            log.error("No document to be loaded - exiting.");
            return null;
        }

        EcmFile addedToArkCase = getFileService().upload(
                nodeName,
                findFileType(cmisDocument),
                "Document",
                cmisDocument.getContentStream().getStream(),
                cmisDocument.getContentStreamMimeType(),
                nodeName,
                new UsernamePasswordAuthenticationToken(userId, userId),
                targetParentFolder.getCmisFolderId(),
                container.getContainerObjectType(),
                container.getContainerObjectId(),
                targetParentFolder.getCmisRepositoryId(),
                cmisDocument);

        return addedToArkCase;
    }

    /**
     * So subtypes can set the file type as needed.
     *
     * @param cmisDocument
     * @return
     */
    public String findFileType(Document cmisDocument)
    {
        return "other";
    }

    public Document lookupCmisDocument(String cmisRepositoryId, String nodeId)
    {
        try
        {
            CmisObject object = getFileService().findObjectById(cmisRepositoryId, nodeId);
            if (object != null && object instanceof Document)
            {
                return (Document) object;
            }
            else
            {
                return null;
            }
        }
        catch (Exception e)
        {
            log.warn("Could not lookup CMIS document for node with id {}", nodeId);
            return null;
        }
    }

    public AcmContainer lookupArkCaseContainer(Long parentFolderId)
    {
        try
        {
            AcmContainer found = getFolderService().findContainerByFolderId(parentFolderId);
            log.debug("ArkCase has container for folder with id {}", parentFolderId);
            return found;
        }
        catch (AcmObjectNotFoundException e)
        {
            log.warn("No container in ArkCase for folder: {}", parentFolderId);
            return null;
        }
    }

    public EcmFile lookupArkCaseFile(String nodeId, Long parentFolderId)
    {
        try
        {
            EcmFile found = getFileDao().findByCmisFileIdAndFolderId(nodeId, parentFolderId);
            log.debug("ArkCase has file with CMIS ID: {} and folder id:{}", nodeId, found.getId());
            return found;
        }
        catch (NoResultException e)
        {
            log.warn("No such file in ArkCase: {}", nodeId);
            return null;
        }
    }

    public EcmFile lookupArkCaseFile(String fileCmisId)
    {
        try
        {
            List<EcmFile> fileList = getFileDao().findByCmisFileId(fileCmisId);
            if (!fileList.isEmpty())
            {
                EcmFile ecmFile = fileList.get(0);
                log.debug("ArkCase has file with CMIS ID: {} and file id: {}", fileCmisId, ecmFile.getId());
                return ecmFile;
            }
            return null;
        }
        catch (NoResultException e)
        {
            log.warn("No such file in ArkCase: {}", fileCmisId);
            return null;
        }
    }

    public AcmFolder lookupArkCaseFolder(String folderCmisId)
    {
        try
        {
            AcmFolder found = getFolderDao().findByCmisFolderId(folderCmisId);
            if (found != null)
            {
                log.debug("ArkCase has folder with CMIS ID: {} and folder id: {}", folderCmisId, found.getId());
            }
            else
            {
                log.debug("ArkCase does not have folder with CMIS ID {}", folderCmisId);
            }

            return found;
        }
        catch (NoResultException e)
        {
            log.warn("No such folder in ArkCase: {}", folderCmisId);
            return null;
        }
    }

    public AcmFolderDao getFolderDao()
    {
        return folderDao;
    }

    public void setFolderDao(AcmFolderDao folderDao)
    {
        this.folderDao = folderDao;
    }

    public EcmFileDao getFileDao()
    {
        return fileDao;
    }

    public void setFileDao(EcmFileDao fileDao)
    {
        this.fileDao = fileDao;
    }

    public AcmFolderService getFolderService()
    {
        return folderService;
    }

    public void setFolderService(AcmFolderService folderService)
    {
        this.folderService = folderService;
    }

    public EcmFileService getFileService()
    {
        return fileService;
    }

    public void setFileService(EcmFileService fileService)
    {
        this.fileService = fileService;
    }

    public boolean isSearchablePDF(File file, String finalMimeType)
    {
        if (finalMimeType.startsWith("application/pdf"))
        {
            PDDocument doc = null;
            try
            {
                doc = PDDocument.load(file);
                for (int i = 0; i < doc.getNumberOfPages(); ++i)
                {
                    PDPage page = doc.getPage(i);
                    PDResources res = page.getResources();
                    for (COSName fontName : res.getFontNames())
                    {
                        PDFont font = res.getFont(fontName);
                        if (font != null)
                        {
                            return true;
                        }
                    }
                }
            }
            catch (IOException e)
            {
                log.error("Unable to load pdf document: {}", e.getMessage(), e);
            }
            return false;
        }
        return false;
    }
}