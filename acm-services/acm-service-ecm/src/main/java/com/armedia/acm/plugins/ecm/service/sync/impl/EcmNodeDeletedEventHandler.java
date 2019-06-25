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

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.sync.EcmEvent;
import com.armedia.acm.plugins.ecm.model.sync.EcmEventType;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationListener;

import javax.persistence.PersistenceException;

/**
 * @author ivana.shekerova on 2/25/2019.
 */
public class EcmNodeDeletedEventHandler implements ApplicationListener<EcmEvent>
{

    private transient final Logger log = LogManager.getLogger(getClass());
    private static String DELETE_MESSAGE = "The %s you were working on was deleted from the content repository.";
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private AcmFolderService folderService;
    private EcmFileService fileService;
    private FolderAndFilesUtils folderAndFilesUtils;

    public void onEcmNodeDeleted(EcmEvent ecmEvent)
    {

        getAuditPropertyEntityAdapter().setUserId(ecmEvent.getUserId());

        EcmFile deleteFile = getFolderAndFilesUtils().lookupArkCaseFile(ecmEvent.getNodeId());
        if (deleteFile != null)
        {
            try
            {
                String message = String.format(DELETE_MESSAGE, deleteFile.getObjectType().toLowerCase());
                getFileService().removeLockAndSendMessage(deleteFile.getId(), message);

                getFileService().deleteFileFromArkcase(deleteFile.getId());
                log.info("Deleted file with CMIS ID [{}]", ecmEvent.getNodeId());
            }
            catch (PersistenceException e)
            {
                log.error("Could not delete file with id [{}], {} ", deleteFile.getId(), e.getMessage(), e);
            }
        }
        else
        {
            AcmFolder deleteFolder = getFolderAndFilesUtils().lookupArkCaseFolder(ecmEvent.getNodeId());
            if (deleteFolder != null)
            {
                String message = String.format(DELETE_MESSAGE, deleteFolder.getObjectType().toLowerCase());
                getFolderService().removeLockAndSendMessage(deleteFolder.getId(), message);

                getFolderService().deleteFolderContent(deleteFolder, AuthenticationUtils.getUsername());

                log.info("Deleted folder with CMIS ID [{}]", ecmEvent.getNodeId());
            }
        }
    }

    protected boolean isDeleteEvent(EcmEvent ecmEvent)
    {
        return EcmEventType.DELETE.equals(ecmEvent.getEcmEventType());
    }

    @Override
    public void onApplicationEvent(EcmEvent ecmEvent)
    {
        if (isDeleteEvent(ecmEvent))
        {
            onEcmNodeDeleted(ecmEvent);
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
