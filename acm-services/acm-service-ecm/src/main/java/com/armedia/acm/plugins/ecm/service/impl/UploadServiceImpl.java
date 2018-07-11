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
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.FileDetails;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.service.UploadService;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;
import com.armedia.acm.web.api.MDCConstants;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

public class UploadServiceImpl implements UploadService {

    private FolderAndFilesUtils folderAndFilesUtils;
    private EcmFileService ecmFileService;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    @Override
    @Async
    @Transactional
    public void mergeAndUploadFiles(FileDetails fileDetails, AcmFolder folder, Document existingFile, Authentication authentication) throws AcmUserActionFailedException, AcmCreateObjectFailedException {
        if (fileDetails != null && fileDetails.getParts() != null && !fileDetails.getParts().isEmpty()) {
            try (SequenceInputStream seq = new SequenceInputStream(getInputStreams(fileDetails.getParts()))) {

                getAuditPropertyEntityAdapter().setUserId(authentication.getName());
                setRepositoryRequestUserAndId(authentication);

                String uniqueFileName = folderAndFilesUtils.createUniqueIdentificator(fileDetails.getName());

                EcmFile metadata = new EcmFile();
                metadata.setFileType(fileDetails.getFileType());
                metadata.setCategory("Document");
                metadata.setFileActiveVersionMimeType(fileDetails.getMimeType());
                metadata.setFileName(fileDetails.getName());
                metadata.setCmisRepositoryId(folder.getCmisRepositoryId());

                ecmFileService.upload(authentication, fileDetails.getObjectType(), fileDetails.getObjectId(), folder.getCmisFolderId(),
                        uniqueFileName, seq, metadata, existingFile);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                deleteFilesQuietly(fileDetails.getParts());
            }
        }
    }

    private void setRepositoryRequestUserAndId(Authentication authentication) {
        String alfrescoUser = "admin";

        if (authentication != null && authentication.getName() != null) {
            alfrescoUser = StringUtils.substringBeforeLast(authentication.getName(), "@");
        }

        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, alfrescoUser);
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, UUID.randomUUID().toString());
    }

    private void deleteFilesQuietly(List<String> parts) {
        String dirPath = System.getProperty("java.io.tmpdir");
        for (String part : parts) {
            org.mule.util.FileUtils.deleteQuietly(new File(dirPath + "/" + part));
        }
    }

    private Enumeration<InputStream> getInputStreams(List<String> parts) throws IOException {
        String dirPath = System.getProperty("java.io.tmpdir");
        Vector<InputStream> inputStream = new Vector<>();
        for (String part : parts) {
            InputStream stream = org.mule.util.FileUtils.openInputStream(new File(dirPath + "/" + part));
            inputStream.addElement(stream);
        }
        return inputStream.elements();
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
}
