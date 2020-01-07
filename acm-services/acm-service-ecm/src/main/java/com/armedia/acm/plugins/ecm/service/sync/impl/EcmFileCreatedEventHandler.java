package com.armedia.acm.plugins.ecm.service.sync.impl;

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
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.sync.EcmEvent;
import com.armedia.acm.plugins.ecm.model.sync.EcmEventType;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;
import com.armedia.acm.web.api.MDCConstants;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.slf4j.MDC;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.UUID;

/**
 * Created by dmiller on 5/17/17.
 */
public class EcmFileCreatedEventHandler implements ApplicationListener<EcmEvent>
{

    private transient final Logger log = LogManager.getLogger(getClass());
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private AcmFolderService folderService;
    private EcmFileService fileService;
    private FolderAndFilesUtils folderAndFilesUtils;

    public void onEcmFileCreated(EcmEvent ecmEvent)
    {

        getAuditPropertyEntityAdapter().setUserId(ecmEvent.getUserId());

        // parent folder must already be in ArkCase
        AcmFolder parentFolder = getFolderAndFilesUtils().lookupArkCaseFolder(ecmEvent.getParentNodeId());
        if (parentFolder == null)
        {
            log.debug("Parent folder is not in ArkCase, so not creating the new file {}", ecmEvent.getNodeId());
            return;
        }

        if (getFolderAndFilesUtils().lookupArkCaseFile(ecmEvent.getNodeId(), parentFolder.getId()) != null)
        {
            log.debug("ArkCase already has the file with CMIS id {}", ecmEvent.getNodeId());
            return;
        }

        AcmContainer container = getFolderAndFilesUtils().lookupArkCaseContainer(parentFolder.getId());
        if (container == null)
        {
            log.debug("Can't find container for the new file with id {}, exiting.", ecmEvent.getNodeId());
            return;
        }

        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, ecmEvent.getUserId());
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, UUID.randomUUID().toString());

        String cmisRepositoryId = getFolderService().getCmisRepositoryId(parentFolder);

        Document cmisDocument = getFolderAndFilesUtils().lookupCmisDocument(cmisRepositoryId, ecmEvent.getNodeId());
        if (cmisDocument == null)
        {
            log.error("No document to be loaded - exiting.");
            return;
        }

        try
        {

            EcmFile addedToArkCase = getFileService().upload(
                    ecmEvent.getNodeName(),
                    getFolderAndFilesUtils().findFileType(cmisDocument),
                    "Document",
                    cmisDocument.getContentStream().getStream(),
                    cmisDocument.getContentStreamMimeType(),
                    ecmEvent.getNodeName(),
                    new UsernamePasswordAuthenticationToken(ecmEvent.getUserId(), ecmEvent.getUserId()),
                    parentFolder.getCmisFolderId(),
                    container.getContainerObjectType(),
                    container.getContainerObjectId(),
                    parentFolder.getCmisRepositoryId(),
                    cmisDocument);
            log.info("Added file to ArkCase with CMIS ID [{}] and ArkCase ID [{}]", ecmEvent.getNodeId(), addedToArkCase.getId());

        }
        catch (AcmCreateObjectFailedException | AcmUserActionFailedException e)
        {
            log.error("Could not add file with CMIS ID [{}] to ArkCase: {}", ecmEvent.getNodeId(), e.getMessage(), e);
        }

    }

    protected boolean isNewFileEvent(EcmEvent ecmEvent)
    {
        return EcmEventType.CREATE.equals(ecmEvent.getEcmEventType()) &&
                EcmFileConstants.ECM_SYNC_NODE_TYPE_DOCUMENT.equals(ecmEvent.getNodeType());
    }

    @Override
    public void onApplicationEvent(EcmEvent ecmEvent)
    {
        if (isNewFileEvent(ecmEvent))
        {
            onEcmFileCreated(ecmEvent);
        }
    }

    public AcmFolderService getFolderService()
    {
        return folderService;
    }

    public void setFolderService(AcmFolderService folderService)
    {
        this.folderService = folderService;
    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

    public EcmFileService getFileService()
    {
        return fileService;
    }

    public void setFileService(EcmFileService fileService)
    {
        this.fileService = fileService;
    }

    public FolderAndFilesUtils getFolderAndFilesUtils()
    {
        return folderAndFilesUtils;
    }

    public void setFolderAndFilesUtils(FolderAndFilesUtils folderAndFilesUtils)
    {
        this.folderAndFilesUtils = folderAndFilesUtils;
    }
}
