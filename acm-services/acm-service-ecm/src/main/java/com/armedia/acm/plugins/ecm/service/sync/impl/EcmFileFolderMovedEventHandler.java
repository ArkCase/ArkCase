package com.armedia.acm.plugins.ecm.service.sync.impl;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.sync.EcmEvent;
import com.armedia.acm.plugins.ecm.model.sync.EcmEventType;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationListener;

/**
 * @author ivana.shekerova on 12/21/2018.
 */
public class EcmFileFolderMovedEventHandler implements ApplicationListener<EcmEvent>
{

    private transient final Logger log = LogManager.getLogger(getClass());
    private static String MOVED_MESSAGE = "The %s you were working on was moved from the content repository.";
    private static String MOVED_INTO_MESSAGE = "Into the folder you were working on was added %s from the content repository.";
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private EcmFileService fileService;
    private AcmFolderService folderService;
    private FolderAndFilesUtils folderAndFilesUtils;

    public void onEcmFileMoved(EcmEvent ecmEvent)
    {
        getAuditPropertyEntityAdapter().setUserId(ecmEvent.getUserId());

        AcmFolder sourceParentFolder = getFolderAndFilesUtils().lookupArkCaseFolder(ecmEvent.getSourceParentNodeId());
        AcmFolder targetParentFolder = getFolderAndFilesUtils().lookupArkCaseFolder(ecmEvent.getTargetParentNodeId());

        if (ecmEvent.getNodeType().equals(EcmFileConstants.ECM_SYNC_NODE_TYPE_DOCUMENT))
        {
            if (sourceParentFolder != null && targetParentFolder != null)
            {
                moveFile(ecmEvent, sourceParentFolder, targetParentFolder);
            }
            else if (sourceParentFolder != null && targetParentFolder == null)
            {
                deleteFile(ecmEvent, sourceParentFolder);
            }
            else if (sourceParentFolder == null && targetParentFolder != null)
            {
                try
                {
                    String message = String.format(MOVED_INTO_MESSAGE, ecmEvent.getNodeType());
                    getFolderService().removeLockAndSendMessage(targetParentFolder.getId(), message);

                    getFolderAndFilesUtils().uploadFile(ecmEvent, targetParentFolder);
                }
                catch (AcmCreateObjectFailedException | AcmUserActionFailedException e)
                {
                    log.error("Could not add file with CMIS ID [{}] to ArkCase: {}", ecmEvent.getNodeId(), e.getMessage(), e);
                }
            }
        }
        else if (ecmEvent.getNodeType().equals(EcmFileConstants.ECM_SYNC_NODE_TYPE_FOLDER))
        {
            if (sourceParentFolder != null && targetParentFolder != null)
            {
                moveFolder(ecmEvent, targetParentFolder);
            }
            else if (sourceParentFolder != null && targetParentFolder == null)
            {
                deleteFolder(ecmEvent);
            }
            else if (sourceParentFolder == null && targetParentFolder != null)
            {
                AcmFolder created = null;
                try
                {
                    String message = String.format(MOVED_INTO_MESSAGE, ecmEvent.getNodeType());
                    getFolderService().removeLockAndSendMessage(targetParentFolder.getId(), message);

                    created = getFolderService().createFolder(targetParentFolder, ecmEvent.getNodeId(), ecmEvent.getNodeName());
                }
                catch (AcmUserActionFailedException | AcmFolderException e)
                {
                    log.debug("Can't create new folder with node id [{}]", ecmEvent.getNodeId());
                }

                try
                {
                    getFolderService().recordMetadataOfExistingFolderChildren(created, ecmEvent.getUserId());
                }
                catch (AcmUserActionFailedException | AcmObjectNotFoundException e)
                {
                    log.debug("Can not create folder children for folder with id: {} and name: {} ", created.getId(), created.getName());
                }

                log.debug("Finished creating new folder with node id {}, ArkCase id {}", created.getCmisFolderId(), created.getId());
            }
        }
    }

    private void deleteFile(EcmEvent ecmEvent, AcmFolder sourceParentFolder)
    {
        // delete the file, since target folder is not in arkcase
        try
        {
            EcmFile arkCaseFile = getFolderAndFilesUtils().lookupArkCaseFile(ecmEvent.getNodeId(), sourceParentFolder.getId());
            if (arkCaseFile != null)
            {
                String message = String.format(MOVED_MESSAGE, arkCaseFile.getObjectType());
                getFileService().removeLockAndSendMessage(arkCaseFile.getFileId(), message);

                getFileService().deleteFileInArkcase(arkCaseFile);
                log.info("Deleted file with CMIS ID [{}]", ecmEvent.getNodeId());
            }
        }
        catch (AcmUserActionFailedException | AcmObjectNotFoundException e)
        {
            log.error("Could not delete file with CMIS ID [{}] to ArkCase: {}", ecmEvent.getNodeId(), e.getMessage(), e);
        }
    }

    private void deleteFolder(EcmEvent ecmEvent)
    {
        // delete the folder, since target folder is not in arkcase
        AcmFolder arkCaseFolder = getFolderAndFilesUtils().lookupArkCaseFolder(ecmEvent.getNodeId());
        if (arkCaseFolder != null)
        {
            String message = String.format(MOVED_MESSAGE, arkCaseFolder.getObjectType());
            getFolderService().removeLockAndSendMessage(arkCaseFolder.getId(), message);

            getFolderService().deleteFolderContent(arkCaseFolder, AuthenticationUtils.getUsername());
            log.info("Deleted folder with CMIS ID [{}]", ecmEvent.getNodeId());
        }
    }

    private EcmFile moveFile(EcmEvent ecmEvent, AcmFolder sourceParentFolder, AcmFolder targetParentFolder)
    {
        EcmFile arkCaseFile = getFolderAndFilesUtils().lookupArkCaseFile(ecmEvent.getNodeId(), sourceParentFolder.getId());
        if (arkCaseFile != null)
        {
            try
            {
                String message = String.format(MOVED_MESSAGE, arkCaseFile.getObjectType());
                getFileService().removeLockAndSendMessage(arkCaseFile.getId(), message);

                EcmFile movedFile = getFileService().moveFileInArkcase(arkCaseFile, targetParentFolder, ecmEvent.getTargetParentNodeType());
                log.info("Moved file to ArkCase with CMIS ID [{}] and ArkCase ID [{}]", ecmEvent.getNodeId(), movedFile.getId());
                return movedFile;
            }
            catch (AcmUserActionFailedException | AcmCreateObjectFailedException e)
            {
                log.error("Could not move file with CMIS ID [{}] to ArkCase: {}", ecmEvent.getNodeId(), e.getMessage(), e);
            }
        }
        return arkCaseFile;
    }

    private AcmFolder moveFolder(EcmEvent ecmEvent, AcmFolder targetParentFolder)
    {
        AcmFolder arkCaseFolder = getFolderAndFilesUtils().lookupArkCaseFolder(ecmEvent.getNodeId());
        if (arkCaseFolder != null)
        {
            try
            {
                String message = String.format(MOVED_MESSAGE, arkCaseFolder.getObjectType());
                getFolderService().removeLockAndSendMessage(arkCaseFolder.getId(), message);

                AcmFolder movedFolder = getFolderService().moveFolderInArkcase(arkCaseFolder, targetParentFolder);
                log.info("Moved folder to ArkCase with CMIS ID [{}] and ArkCase ID [{}]", ecmEvent.getNodeId(), movedFolder.getId());
                return movedFolder;
            }
            catch (AcmUserActionFailedException | AcmObjectNotFoundException | AcmFolderException e)
            {
                log.error("Could not move folder with CMIS ID [{}] to ArkCase: {}", ecmEvent.getNodeId(), e.getMessage(), e);
            }
        }
        return arkCaseFolder;
    }

    protected boolean isMoveFileEvent(EcmEvent ecmEvent)
    {
        return EcmEventType.MOVE.equals(ecmEvent.getEcmEventType());
    }

    @Override
    public void onApplicationEvent(EcmEvent ecmEvent)
    {
        if (isMoveFileEvent(ecmEvent))
        {
            onEcmFileMoved(ecmEvent);
        }
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

    public AcmFolderService getFolderService()
    {
        return folderService;
    }

    public void setFolderService(AcmFolderService folderService)
    {
        this.folderService = folderService;
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
