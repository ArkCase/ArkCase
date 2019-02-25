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

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.sync.EcmEvent;
import com.armedia.acm.plugins.ecm.model.sync.EcmEventType;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.util.List;

/**
 * @author ivana.shekerova on 1/4/2019.
 */
public class EcmFileFolderCopiedEventHandler implements ApplicationListener<EcmEvent>
{

    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private EcmFileDao fileDao;
    private EcmFileService fileService;
    private AcmFolderService folderService;
    private FolderAndFilesUtils folderAndFilesUtils;

    public void onEcmFileCopied(EcmEvent ecmEvent)
    {
        getAuditPropertyEntityAdapter().setUserId(ecmEvent.getUserId());

        // if the copied file's folder ID is not an ArkCase folder, then the file was copied to a
        // non-ArkCase destination, and no further work is needed
        AcmFolder targetParentFolder = getFolderAndFilesUtils().lookupArkCaseFolder(ecmEvent.getParentNodeId());
        if (targetParentFolder == null)
        {
            log.debug("Can't find folder for the copied file with id {}, no further action.", ecmEvent.getNodeId());
            return;
        }

        if (ecmEvent.getNodeType().equals(EcmFileConstants.ECM_SYNC_NODE_TYPE_DOCUMENT))
        {
            // If it already exists, the copy has already been done in ArkCase, and no further work is needed
            EcmFile arkCaseFile = getFolderAndFilesUtils().lookupArkCaseFile(ecmEvent.getNodeId(), targetParentFolder.getId());
            if (targetParentFolder != null && arkCaseFile == null)
            {
                List<EcmFile> listFiles = getFileDao().findByCmisFileId(ecmEvent.getSourceOfCopyNodeId());
                if (listFiles.isEmpty())
                {
                    // not in arkcase, so the folder that the file was copied from is not an Arkcase folder
                    getFolderAndFilesUtils().uploadFile(ecmEvent, targetParentFolder);
                }
                else
                {
                    // in arkcase, so the folder that the file was copied from is Arkcase folder
                    copyFile(ecmEvent, targetParentFolder, listFiles);
                }
            }
        }
        else if (ecmEvent.getNodeType().equals(EcmFileConstants.ECM_SYNC_NODE_TYPE_FOLDER))
        {
            // If it already exists, the copy has already been done in ArkCase, and no further work is needed
            AcmFolder arkCaseFolder = getFolderAndFilesUtils().lookupArkCaseFolder(ecmEvent.getNodeId());
            if (targetParentFolder != null && arkCaseFolder == null)
            {
                    AcmFolder created = getFolderService().createFolder(targetParentFolder, ecmEvent.getNodeId(), ecmEvent.getNodeName());
                    try
                    {
                    getFolderService().createFolderChildrenInArkcase(created, ecmEvent.getUserId());
                    }
                    catch (AcmUserActionFailedException | AcmObjectNotFoundException e)
                    {
                        log.debug("Can not create folder children for folder with id: {} and name: {} ", created.getId(),
                                created.getName());
                    }

                    log.debug("Finished creating new folder with node id {}, ArkCase id {}", created.getCmisFolderId(), created.getId());
            }
            else if (targetParentFolder != null && arkCaseFolder != null)
            {
                try
                {
                    List<AcmObject> acmObjects = getFolderService().getFolderChildren(arkCaseFolder.getId());
                    if (acmObjects.isEmpty())
                    {
                        getFolderService().createFolderChildrenInArkcase(arkCaseFolder, ecmEvent.getUserId());
                    }
                }
                catch (AcmUserActionFailedException | AcmObjectNotFoundException e)
                {
                    log.error("Could not create children in folder with CMIS ID [{}] to ArkCase: {}", ecmEvent.getNodeId(), e.getMessage(),
                            e);
                }

            }
        }
    }

    private EcmFile copyFile(EcmEvent ecmEvent, AcmFolder targetParentFolder, List<EcmFile> listFiles)
    {
        EcmFile originalFile = listFiles.get(0);
        EcmFile copiedFile = null;
        try
        {
            copiedFile = getFileService().copyFileInArkcase(originalFile, ecmEvent.getNodeId(), targetParentFolder);
        }
        catch (AcmUserActionFailedException e)
        {
            log.error("Could not copy file with CMIS ID [{}] to ArkCase: {}", ecmEvent.getNodeId(), e.getMessage(), e);
        }
        return copiedFile;
    }

    protected boolean isCopyFileEvent(EcmEvent ecmEvent)
    {
        return EcmEventType.COPY.equals(ecmEvent.getEcmEventType());
    }

    @Override
    public void onApplicationEvent(EcmEvent ecmEvent)
    {
        if (isCopyFileEvent(ecmEvent))
        {
            onEcmFileCopied(ecmEvent);
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

    public EcmFileDao getFileDao()
    {
        return fileDao;
    }

    public void setFileDao(EcmFileDao fileDao)
    {
        this.fileDao = fileDao;
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
