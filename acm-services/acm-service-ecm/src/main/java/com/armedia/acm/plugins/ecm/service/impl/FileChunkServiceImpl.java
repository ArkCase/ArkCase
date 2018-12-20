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
import static com.armedia.acm.plugins.ecm.service.impl.FileUploadProgressNotifierMessageBuilder.OBJECT_TYPE;

import com.armedia.acm.data.AcmProgressEvent;
import com.armedia.acm.data.AcmProgressIndicator;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.FileChunkDetails;
import com.armedia.acm.plugins.ecm.model.FileDetails;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.service.FileChunkService;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;
import com.armedia.acm.web.api.MDCConstants;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.*;

public class FileChunkServiceImpl implements FileChunkService {

    private Logger log = LoggerFactory.getLogger(getClass());

    private FolderAndFilesUtils folderAndFilesUtils;
    private EcmFileService ecmFileService;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private AcmProgressIndicator acmProgressIndicator;
    private ApplicationEventPublisher applicationEventPublisher;


    @Override
    @Async
    @Transactional
    public void mergeAndUploadFiles(FileDetails fileDetails, AcmFolder folder, Document existingFile, Authentication authentication)
            throws IOException {
        //initiate the counter on the entry afterwards for each phase increment by calling the calculateProgress() method

        //get the progressIndicator from 50% to 100%
        SequenceInputStream inputStream = mergeChunks(fileDetails);

        getAuditPropertyEntityAdapter().setUserId(authentication.getName());
        setRepositoryRequestUserAndId(authentication);
        String uniqueFileName = folderAndFilesUtils.createUniqueIdentificator(fileDetails.getName());

        EcmFile metadata = new EcmFile();
        metadata.setFileType(fileDetails.getFileType());
        metadata.setCategory("Document");
        metadata.setFileActiveVersionMimeType(fileDetails.getMimeType());
        metadata.setFileName(fileDetails.getName());
        metadata.setCmisRepositoryId(folder.getCmisRepositoryId());

        log.debug("Start uploading the file with name {}", uniqueFileName);

        FileUploadProgressIndicator progressIndicator = new FileUploadProgressIndicator();
        progressIndicator.setObjectId(metadata.getId());
        progressIndicator.setObjectType(OBJECT_TYPE);
        progressIndicator.setUser(authentication.getName());

        int uploadedSuccessfully = 0, uploadFailed= 0;

        try {
            ecmFileService.upload(authentication, fileDetails.getObjectType(), fileDetails.getObjectId(), folder.getCmisFolderId(),
                    uniqueFileName, inputStream, metadata, existingFile);
            progressIndicator.setProgress(++uploadedSuccessfully);
            applicationEventPublisher.publishEvent(new AcmProgressEvent(progressIndicator));

        } catch (Exception e){
            log.warn("File was not uploadded sucessfully. TODO HANDLE EXCEPTIONS BETTER", e);
            progressIndicator.setProgressFailed(++uploadFailed);
            applicationEventPublisher.publishEvent(new AcmProgressEvent(progressIndicator));
        }
        log.debug("Start deleting the temporary parts from the file {}", fileDetails.getParts());
        //acmProgressIndicator.setProgress(calculateProgress(progressCounter, 50)); // getfrom50to100%
        //applicationEventPublisher.publishEvent(new AcmProgressEvent(acmProgressIndicator));
        //progressCounter++;

        deleteFilesQuietly(fileDetails.getParts());

    }

    @Override
    public SequenceInputStream mergeChunks(FileDetails fileDetails)
            throws IOException {
        SequenceInputStream inputStream = null;
        if (fileDetails != null && fileDetails.getParts() != null && !fileDetails.getParts().isEmpty()) {
            inputStream = new SequenceInputStream(getInputStreams(fileDetails.getParts()));
        }
        return inputStream;
    }

    private void setRepositoryRequestUserAndId(Authentication authentication) {
        String alfrescoUser = "admin";

        if (authentication != null && authentication.getName() != null) {
            alfrescoUser = StringUtils.substringBeforeLast(authentication.getName(), "@");
        }

        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, alfrescoUser);
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, UUID.randomUUID().toString());
    }

    private void deleteFilesQuietly(List<FileChunkDetails> parts) {
        String dirPath = System.getProperty("java.io.tmpdir");
        for (FileChunkDetails part : parts) {
            org.mule.util.FileUtils.deleteQuietly(new File(dirPath + "/" + part.getFileName()));
        }
    }

    private Enumeration<InputStream> getInputStreams(List<FileChunkDetails> parts) throws IOException {
        String dirPath = System.getProperty("java.io.tmpdir");
        Vector<InputStream> inputStream = new Vector<>();
        for (FileChunkDetails part : parts) {
            InputStream stream = org.mule.util.FileUtils.openInputStream(new File(dirPath + "/" + part.getFileName()));
            inputStream.addElement(stream);
        }
        return inputStream.elements();
    }

    private int calculateProgress(int current, int total)
    {
        return (int) (current * 1.0 / total * 50);
    }

    public FolderAndFilesUtils getFolderAndFilesUtils() {
        return folderAndFilesUtils;
    }

    public void setFolderAndFilesUtils(FolderAndFilesUtils folderAndFilesUtils) {
        this.folderAndFilesUtils = folderAndFilesUtils;
    }

    public EcmFileService getEcmFileService() {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService) {
        this.ecmFileService = ecmFileService;
    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter() {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter) {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

    public AcmProgressIndicator getAcmProgressIndicator() {
        return acmProgressIndicator;
    }

    public void setAcmProgressIndicator(AcmProgressIndicator acmProgressIndicator) {
        this.acmProgressIndicator = acmProgressIndicator;
    }

    public ApplicationEventPublisher getApplicationEventPublisher() {
        return applicationEventPublisher;
    }

    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
