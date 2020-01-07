package com.armedia.acm.plugins.outlook.service.impl;

/*-
 * #%L
 * ACM Extra Plugin: MS Outlook Integration
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

import com.armedia.acm.core.exceptions.AcmOutlookCreateItemFailedException;
import com.armedia.acm.core.exceptions.AcmOutlookItemNotFoundException;
import com.armedia.acm.plugins.ecm.dao.AcmContainerDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.outlook.service.OutlookContainerCalendarService;
import com.armedia.acm.plugins.profile.service.UserOrgService;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.model.OutlookConfig;
import com.armedia.acm.service.outlook.model.OutlookFolder;
import com.armedia.acm.service.outlook.model.OutlookFolderPermission;
import com.armedia.acm.service.outlook.service.OutlookFolderService;
import com.armedia.acm.service.outlook.service.OutlookService;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

import microsoft.exchange.webservices.data.core.enumeration.permission.folder.FolderPermissionLevel;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.enumeration.service.DeleteMode;

/**
 * Created by nebojsha on 25.05.2015.
 */
public class OutlookContainerCalendarServiceImpl implements OutlookContainerCalendarService
{

    private final Logger log = LogManager.getLogger(getClass());

    private OutlookFolderService outlookFolderService;
    private AcmContainerDao acmContainerDao;
    private UserDao userDao;
    private UserOrgService userOrgService;
    private OutlookService outlookService;
    private OutlookConfig outlookConfig;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public OutlookFolder createFolder(AcmOutlookUser outlookUser, Long objectId, String objectType, String folderName,
            AcmContainer container, List<AcmParticipant> participants)
            throws AcmOutlookItemNotFoundException, AcmOutlookCreateItemFailedException
    {

        OutlookFolder outlookFolder = new OutlookFolder();
        outlookFolder.setDisplayName(folderName);

        List<OutlookFolderPermission> permissions = mapParticipantsToFolderPermission(participants);
        outlookFolder.setPermissions(permissions);
        outlookFolder = outlookFolderService.createFolder(outlookUser, objectId, objectType, WellKnownFolderName.Calendar, outlookFolder);

        container.setCalendarFolderId(outlookFolder.getId());

        return outlookFolder;
    }

    @Override
    @Transactional
    public void deleteFolder(AcmOutlookUser outlookUser, Long containerId, String folderId, DeleteMode deleteMode)
            throws AcmOutlookItemNotFoundException
    {
        AcmContainer container = acmContainerDao.find(containerId);
        deleteFolder(outlookUser, container, deleteMode);
    }

    @Override
    @Transactional
    public void deleteFolder(AcmOutlookUser outlookUser, AcmContainer container, DeleteMode deleteMode)
            throws AcmOutlookItemNotFoundException
    {
        outlookFolderService.deleteFolder(outlookUser, container.getCalendarFolderId(), deleteMode);
        container.setCalendarFolderId(null);
        acmContainerDao.save(container);
    }

    @Override
    public void updateFolderParticipants(AcmOutlookUser outlookUser, String folderId, List<AcmParticipant> participants)
            throws AcmOutlookItemNotFoundException
    {

        outlookFolderService.updateFolderPermissions(outlookUser, folderId, mapParticipantsToFolderPermission(participants));

    }

    private List<OutlookFolderPermission> mapParticipantsToFolderPermission(List<AcmParticipant> participantsForObject)
    {
        List<OutlookFolderPermission> folderPermissionsToBeAdded = new LinkedList<>();
        if (outlookConfig.getParticipantTypes() == null || outlookConfig.getParticipantTypes().isEmpty())
        {
            // this will cause all permissions in folder to be removed
            log.warn("There are not defined participants types to include");
        }
        else
        {
            for (AcmParticipant ap : participantsForObject)
            {
                if (outlookConfig.getParticipantTypes().contains(ap.getParticipantType()))
                {
                    // add participant to access calendar folder
                    AcmUser user = userDao.findByUserId(ap.getParticipantLdapId());
                    if (user == null)
                    {
                        continue;
                    }
                    OutlookFolderPermission outlookFolderPermission = new OutlookFolderPermission();
                    outlookFolderPermission.setEmail(user.getMail());
                    switch (ap.getParticipantType())
                    {
                    case "follower":
                        if (outlookConfig.getFollowerAccess() != null)
                        {
                            outlookFolderPermission.setLevel(FolderPermissionLevel.valueOf(outlookConfig.getFollowerAccess()));
                            break;
                        }
                        else
                        {
                            outlookFolderPermission.setLevel(FolderPermissionLevel.PublishingEditor);
                            break;
                        }
                    case "assignee":
                        if (outlookConfig.getAssigneeAccess() != null)
                        {
                            outlookFolderPermission.setLevel(FolderPermissionLevel.valueOf(outlookConfig.getAssigneeAccess()));
                            break;
                        }
                        else
                        {
                            outlookFolderPermission.setLevel(FolderPermissionLevel.Author);
                            break;
                        }
                    case "approver":
                        if (outlookConfig.getApproverAccess() != null)
                        {
                            outlookFolderPermission.setLevel(FolderPermissionLevel.valueOf(outlookConfig.getApproverAccess()));
                            break;
                        }
                        else
                        {
                            outlookFolderPermission.setLevel(FolderPermissionLevel.Reviewer);
                            break;
                        }
                    default:
                        if (outlookConfig.getDefaultAccess() != null)
                        {
                            outlookFolderPermission.setLevel(FolderPermissionLevel.valueOf(outlookConfig.getDefaultAccess()));
                            break;
                        }
                        else
                        {
                            outlookFolderPermission.setLevel(FolderPermissionLevel.None);
                            break;
                        }
                    }
                    folderPermissionsToBeAdded.add(outlookFolderPermission);
                }
            }
        }
        return folderPermissionsToBeAdded;
    }

    public OutlookFolderService getOutlookFolderService()
    {
        return outlookFolderService;
    }

    public void setOutlookFolderService(OutlookFolderService outlookFolderService)
    {
        this.outlookFolderService = outlookFolderService;
    }

    public AcmContainerDao getAcmContainerDao()
    {
        return acmContainerDao;
    }

    public void setAcmContainerDao(AcmContainerDao acmContainerDao)
    {
        this.acmContainerDao = acmContainerDao;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public UserOrgService getUserOrgService()
    {
        return userOrgService;
    }

    public void setUserOrgService(UserOrgService userOrgService)
    {
        this.userOrgService = userOrgService;
    }

    public OutlookService getOutlookService()
    {
        return outlookService;
    }

    public void setOutlookService(OutlookService outlookService)
    {
        this.outlookService = outlookService;
    }

    public OutlookConfig getOutlookConfig()
    {
        return outlookConfig;
    }

    public void setOutlookConfig(OutlookConfig outlookConfig)
    {
        this.outlookConfig = outlookConfig;
    }
}
