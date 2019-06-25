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

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.sync.EcmEvent;
import com.armedia.acm.plugins.ecm.model.sync.EcmEventType;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.impl.EcmFileParticipantService;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationListener;

import javax.persistence.PersistenceException;

/**
 * Created by dmiller on 5/17/17.
 */
public class EcmFolderCreatedEventHandler implements ApplicationListener<EcmEvent>
{
    private transient final Logger log = LogManager.getLogger(getClass());
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private AcmFolderDao folderDao;
    private AcmFolderService folderService;
    private EcmFileParticipantService fileParticipantService;
    private FolderAndFilesUtils folderAndFilesUtils;

    public void onEcmFolderCreated(EcmEvent folderCreated)
    {
        log.debug("Handling new folder {}", folderCreated.getNodeId());

        if (getFolderAndFilesUtils().lookupArkCaseFolder(folderCreated.getNodeId()) != null)
        {
            return;
        }

        AcmFolder parentFolder = getFolderAndFilesUtils().lookupArkCaseFolder(folderCreated.getParentNodeId());
        if (parentFolder == null)
        {
            log.debug("Parent folder is not in ArkCase, so not creating the new folder {}", folderCreated.getNodeId());
            return;
        }

        AcmFolder newFolder = prepareFolder(parentFolder, folderCreated);

        try
        {
            // new folder should have creator and modifier of the user that took the action in the ECM system
            getAuditPropertyEntityAdapter().setUserId(folderCreated.getUserId());
            AcmFolder created = getFolderDao().save(newFolder);

            getFileParticipantService().setFolderParticipantsFromParentFolder(created);
            created = getFolderDao().save(created);

            log.debug("Finished creating new folder with node id {}, ArkCase id {}", folderCreated.getNodeId(), created.getId());
        }
        catch (PersistenceException pe)
        {
            log.error("Cannot create new folder with CMIS ID {}: [{}]", folderCreated.getNodeId(), pe.getMessage(), pe);
        }
    }

    protected AcmFolder prepareFolder(AcmFolder parentFolder, EcmEvent folderCreated)
    {
        AcmFolder folder = new AcmFolder();
        folder.setParentFolder(parentFolder);
        folder.setName(folderCreated.getNodeName());
        folder.setCmisFolderId(folderCreated.getNodeId());

        String cmisRepositoryId = getFolderService().getCmisRepositoryId(parentFolder);
        folder.setCmisRepositoryId(cmisRepositoryId);

        return folder;
    }

    protected boolean isNewFolderEvent(EcmEvent ecmEvent)
    {
        return EcmEventType.CREATE.equals(ecmEvent.getEcmEventType())
                && EcmFileConstants.ECM_SYNC_NODE_TYPE_FOLDER.equals(ecmEvent.getNodeType());
    }

    @Override
    public void onApplicationEvent(EcmEvent ecmEvent)
    {
        if (isNewFolderEvent(ecmEvent))
        {
            onEcmFolderCreated(ecmEvent);
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

    public EcmFileParticipantService getFileParticipantService()
    {
        return fileParticipantService;
    }

    public void setFileParticipantService(EcmFileParticipantService fileParticipantService)
    {
        this.fileParticipantService = fileParticipantService;
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
