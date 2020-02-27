package com.armedia.acm.plugins.ecm.service;

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

import com.armedia.acm.auth.AcmAuthenticationDetails;
import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.event.*;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;

public class FolderEventPublisher implements ApplicationEventPublisherAware
{

    private transient final Logger log = LogManager.getLogger(getClass());
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        eventPublisher = applicationEventPublisher;
    }

    public void publishFolderCreatedEvent(AcmFolder source, Authentication auth, String ipAddress, boolean succeeded)
    {
        log.debug("Publishing a folder created event.");
        AcmFolderCreatedEvent folderCreatedEvent = new AcmFolderCreatedEvent(source, auth.getName(), ipAddress);
        if (auth.getDetails() != null && auth.getDetails() instanceof AcmAuthenticationDetails)
        {
            folderCreatedEvent.setIpAddress(((AcmAuthenticationDetails) auth.getDetails()).getRemoteAddress());
        }
        folderCreatedEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(folderCreatedEvent);
    }

    public void publishFolderCreatedEvent(AcmFolder source, boolean succeeded, String parentObjectType, Long parentObjectId)
    {
        log.debug("Publishing a folder created event.");
        String ipAddress = AuthenticationUtils.getUserIpAddress();
        String userId = AuthenticationUtils.getUsername();

        AcmFolderCreatedEvent folderCreatedEvent = new AcmFolderCreatedEvent(source, userId, ipAddress);
        folderCreatedEvent.setParentObjectId(parentObjectId);
        folderCreatedEvent.setParentObjectType(parentObjectType);
        folderCreatedEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(folderCreatedEvent);
    }

    public void publishFolderDeletedEvent(AcmFolder source, Authentication auth, String ipAddress, boolean succeeded)
    {
        log.debug("Publishing a folder deleted event.");
        AcmFolderDeletedEvent folderDeletedEvent = new AcmFolderDeletedEvent(source, auth.getName(), ipAddress);
        folderDeletedEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(folderDeletedEvent);
    }

    public void publishFolderMovedEvent(AcmFolder source, Authentication auth, String ipAddress, boolean succeeded)
    {
        log.debug("Publishing a folder moved event.");
        AcmFolderMovedEvent folderMovedEvent = new AcmFolderMovedEvent(source, auth.getName(), ipAddress);
        folderMovedEvent.setSucceeded(succeeded);
        eventPublisher.publishEvent(folderMovedEvent);
    }

    public void publishFolderRenamedEvent(AcmFolder source, Authentication auth, String ipAddress, boolean succeeded)
    {
        log.debug("Publishing a folder renamed event.");
        AcmFolderRenamedEvent folderRenamedEvent = new AcmFolderRenamedEvent(source, auth.getName(), ipAddress);
        folderRenamedEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(folderRenamedEvent);
    }

    public void publishFolderRenamedEvent(AcmFolder source, boolean succeeded, String parentObjectType, Long parentObjectId)
    {
        log.debug("Publishing a folder renamed event.");
        String ipAddress = AuthenticationUtils.getUserIpAddress();
        String userId = AuthenticationUtils.getUsername();

        AcmFolderRenamedEvent folderRenamedEvent = new AcmFolderRenamedEvent(source, userId, ipAddress);
        folderRenamedEvent.setSucceeded(succeeded);
        folderRenamedEvent.setParentObjectId(parentObjectId);
        folderRenamedEvent.setParentObjectType(parentObjectType);
        eventPublisher.publishEvent(folderRenamedEvent);
    }

    public void publishFolderCopiedEvent(AcmFolder source, Authentication auth, String ipAddress, boolean succeeded)
    {
        log.debug("Publishing a folder copied event.");
        AcmFolderCopiedEvent folderCopiedEvent = new AcmFolderCopiedEvent(source, auth.getName(), ipAddress);
        folderCopiedEvent.setSucceeded(succeeded);
        eventPublisher.publishEvent(folderCopiedEvent);
    }

    public void publishFolderCopiedAsLinkEvent(AcmFolder source, Authentication auth, String ipAddress, boolean succeeded)
    {
        log.debug("Publishing a folder copied as link event.");
        AcmFolderCopiedAsLinkEvent folderCopiedAsLinkEvent = new AcmFolderCopiedAsLinkEvent(source, auth.getName(), ipAddress);
        folderCopiedAsLinkEvent.setSucceeded(succeeded);
        eventPublisher.publishEvent(folderCopiedAsLinkEvent);
    }


}
