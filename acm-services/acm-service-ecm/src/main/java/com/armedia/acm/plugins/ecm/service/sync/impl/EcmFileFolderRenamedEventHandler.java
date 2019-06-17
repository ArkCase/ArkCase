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
 * @author ivana.shekerova on 1/16/2019.
 */
public class EcmFileFolderRenamedEventHandler implements ApplicationListener<EcmEvent>
{

    private transient final Logger log = LogManager.getLogger(getClass());
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private AcmFolderService folderService;
    private EcmFileService fileService;
    private FolderAndFilesUtils folderAndFilesUtils;

    public void onEcmFileFolderRenamed(EcmEvent ecmEvent)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmFolderException
    {
        getAuditPropertyEntityAdapter().setUserId(ecmEvent.getUserId());

        if (ecmEvent.getNodeType().equals(EcmFileConstants.ECM_SYNC_NODE_TYPE_FOLDER))
        {
            AcmFolder folder = getFolderAndFilesUtils().lookupArkCaseFolder(ecmEvent.getNodeId());
            if (folder != null && !folder.getName().equals(ecmEvent.getNodeName()))
            {
                getFolderService().renameFolder(folder.getId(), ecmEvent.getNodeName());
            }
        }
        else if (ecmEvent.getNodeType().equals(EcmFileConstants.ECM_SYNC_NODE_TYPE_DOCUMENT))
        {
            EcmFile file = getFolderAndFilesUtils().lookupArkCaseFile(ecmEvent.getNodeId());
            if (file != null)
            {
                String fileName = getFileName(ecmEvent, file);
                if (!fileName.equals(ecmEvent.getNodeName()))
                {
                    getFileService().renameFileInArkcase(file, ecmEvent.getNodeName());
                }
            }
        }
    }

    private String getFileName(EcmEvent ecmEvent, EcmFile file)
    {
        String fileName = file.getFileName();
        if (ecmEvent.getNodeName().contains("."))
        {
            fileName = file.getFileName().concat(file.getFileActiveVersionNameExtension());
        }
        return fileName;
    }

    protected boolean isRenameFileFolderEvent(EcmEvent ecmEvent)
    {
        return EcmEventType.RENAME.equals(ecmEvent.getEcmEventType());
    }

    @Override
    public void onApplicationEvent(EcmEvent ecmEvent)
    {
        if (isRenameFileFolderEvent(ecmEvent))
        {
            try
            {
                onEcmFileFolderRenamed(ecmEvent);
            }
            catch (AcmUserActionFailedException | AcmObjectNotFoundException | AcmFolderException e)
            {
                log.error("{} with cmis id {} can not be renamed {}", ecmEvent.getNodeType(), ecmEvent.getNodeId(), e.getMessage());
            }
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

    public FolderAndFilesUtils getFolderAndFilesUtils()
    {
        return folderAndFilesUtils;
    }

    public void setFolderAndFilesUtils(FolderAndFilesUtils folderAndFilesUtils)
    {
        this.folderAndFilesUtils = folderAndFilesUtils;
    }
}
