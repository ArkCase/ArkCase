package com.armedia.acm.plugins.ecm.service.impl;

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
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.ecm.model.*;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.service.FileChunkService;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;
import com.armedia.acm.web.api.MDCConstants;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.*;

public class FileChunkServiceImpl implements FileChunkService
{

    private Logger log = LogManager.getLogger(getClass());

    private FolderAndFilesUtils folderAndFilesUtils;
    private EcmFileService ecmFileService;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private EcmFileUploaderConfig ecmFileUploaderConfig;

    @Override
    @Async
    @Transactional
    public void mergeAndUploadFiles(FileDetails fileDetails, AcmFolder folder, Document existingFile, Authentication authentication)
            throws IOException, AcmUserActionFailedException, AcmCreateObjectFailedException
    {
        SequenceInputStreamHolder holder = mergeChunks(fileDetails);

        getAuditPropertyEntityAdapter().setUserId(authentication.getName());
        setRepositoryRequestUserAndId(authentication);
        String uniqueFileName = folderAndFilesUtils.createUniqueIdentificator(fileDetails.getName());

        EcmFile metadata = new EcmFile();
        metadata.setFileType(fileDetails.getFileType());
        metadata.setCategory("Document");
        metadata.setFileActiveVersionMimeType(fileDetails.getMimeType());
        metadata.setFileName(fileDetails.getName());
        metadata.setCmisRepositoryId(folder.getCmisRepositoryId());
        metadata.setUuid(fileDetails.getUuid());
        metadata.setFileLang(fileDetails.getLang());

        log.debug("Start uploading the file with name {}", uniqueFileName);

        if (existingFile != null)
        {
            // depricated upload method being called, find another solution. So far not implemented in upload large
            // files
            ecmFileService.upload(authentication, fileDetails.getObjectType(), fileDetails.getObjectId(), folder.getCmisFolderId(),
                    uniqueFileName, holder.getStream(), metadata, existingFile);
        }
        else
        {
            AcmMultipartFile multipartFile = new AcmMultipartFile(uniqueFileName, fileDetails.getName(), fileDetails.getMimeType(), false,
                    holder.getSize(), new byte[0], holder.getStream(), true);
            ecmFileService.upload(authentication, multipartFile, folder.getCmisFolderId(), fileDetails.getObjectType(),
                    fileDetails.getObjectId(), metadata);
        }
        log.debug("Start deleting the temporary parts from the file {}", fileDetails.getParts());

        deleteFilesQuietly(fileDetails.getParts());

    }

    @Override
    public SequenceInputStreamHolder mergeChunks(FileDetails fileDetails)
            throws IOException
    {
        SequenceInputStreamHolder holder = new SequenceInputStreamHolder();
        SequenceInputStream inputStream = null;
        Long size = 0L;
        if (fileDetails != null && fileDetails.getParts() != null && !fileDetails.getParts().isEmpty())
        {
            AbstractMap.SimpleEntry entry = getInputStreamsAndSize(fileDetails.getParts());
            inputStream = new SequenceInputStream((Enumeration<? extends InputStream>) entry.getKey());
            size = (Long) entry.getValue();
        }

        holder.setStream(inputStream);
        holder.setSize(size);
        return holder;
    }

    private void setRepositoryRequestUserAndId(Authentication authentication)
    {
        String alfrescoUser = "admin";

        if (authentication != null && authentication.getName() != null)
        {
            alfrescoUser = StringUtils.substringBeforeLast(authentication.getName(), "@");
        }

        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, alfrescoUser);
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, UUID.randomUUID().toString());
    }

    public void deleteFilesQuietly(List<FileChunkDetails> parts)
    {
        String dirPath = System.getProperty("java.io.tmpdir");
        String uniqueArkCaseHashFileIdentifier = ecmFileUploaderConfig.getUniqueHashFileIdentifier();
        for (FileChunkDetails part : parts)
        {
            org.mule.util.FileUtils
                    .deleteQuietly(new File(dirPath + File.separator + uniqueArkCaseHashFileIdentifier + "-" + part.getFileName()));
        }
    }

    private AbstractMap.SimpleEntry<Enumeration<InputStream>, Long> getInputStreamsAndSize(List<FileChunkDetails> parts) throws IOException
    {
        String dirPath = System.getProperty("java.io.tmpdir");
        String uniqueArkCaseHashFileIdentifier = ecmFileUploaderConfig.getUniqueHashFileIdentifier();
        Vector<InputStream> inputStream = new Vector<>();
        Long size = 0L;
        for (FileChunkDetails part : parts)
        {
            File file = new File(dirPath + File.separator + uniqueArkCaseHashFileIdentifier + "-" + part.getFileName());
            InputStream stream = org.mule.util.FileUtils.openInputStream(file);
            inputStream.addElement(stream);
            size += file.length();
        }

        AbstractMap.SimpleEntry<Enumeration<InputStream>, Long> entry = new AbstractMap.SimpleEntry<>(inputStream.elements(), size);
        return entry;
    }

    public FolderAndFilesUtils getFolderAndFilesUtils()
    {
        return folderAndFilesUtils;
    }

    public void setFolderAndFilesUtils(FolderAndFilesUtils folderAndFilesUtils)
    {
        this.folderAndFilesUtils = folderAndFilesUtils;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

    public EcmFileUploaderConfig getEcmFileUploaderConfig()
    {
        return ecmFileUploaderConfig;
    }

    public void setEcmFileUploaderConfig(EcmFileUploaderConfig ecmFileUploaderConfig)
    {
        this.ecmFileUploaderConfig = ecmFileUploaderConfig;
    }
}
