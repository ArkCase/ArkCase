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
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileUpdatedEvent;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.plugins.ecm.model.event.EcmFileActiveVersionSetEvent;
import com.armedia.acm.plugins.ecm.model.event.EcmFileCopiedAsLinkEvent;
import com.armedia.acm.plugins.ecm.model.event.EcmFileCopiedEvent;
import com.armedia.acm.plugins.ecm.model.event.EcmFileCreatedEvent;
import com.armedia.acm.plugins.ecm.model.event.EcmFileDeletedEvent;
import com.armedia.acm.plugins.ecm.model.event.EcmFileMovedEvent;
import com.armedia.acm.plugins.ecm.model.event.EcmFileRenamedEvent;
import com.armedia.acm.plugins.ecm.model.event.EcmFileReplacedEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;

public class FileEventPublisher implements ApplicationEventPublisherAware
{

    private transient final Logger log = LogManager.getLogger(getClass());
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        eventPublisher = applicationEventPublisher;
    }

    public void publishFileCreatedEvent(EcmFile source, Authentication auth, String ipAddress, boolean succeeded)
    {

        log.debug("Publishing a file created event.");
        EcmFileCreatedEvent fileCreatedEvent = new EcmFileCreatedEvent(source, auth.getName(), ipAddress);
        if (auth.getDetails() != null && auth.getDetails() instanceof AcmAuthenticationDetails)
        {
            fileCreatedEvent.setIpAddress(((AcmAuthenticationDetails) auth.getDetails()).getRemoteAddress());
        }
        fileCreatedEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(fileCreatedEvent);
    }

    public void publishFileDeletedEvent(EcmFile source, Authentication auth, String ipAddress, boolean succeeded)
    {

        log.debug("Publishing a file deleted event.");
        EcmFileDeletedEvent fileDeletedEvent = new EcmFileDeletedEvent(source, auth.getName(), ipAddress);
        fileDeletedEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(fileDeletedEvent);
    }

    public void publishFileCopiedEvent(EcmFile source, EcmFile original, Authentication auth, String ipAddress, boolean succeeded)
    {

        log.debug("Publishing a file copied event.");
        EcmFileCopiedEvent filedCopiedEvent = new EcmFileCopiedEvent(source, original, auth.getName(), ipAddress);
        filedCopiedEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(filedCopiedEvent);
    }

    public void publishFileMovedEvent(EcmFile source, Authentication auth, String ipAddress, boolean succeeded)
    {

        log.debug("Publishing a file moved event.");
        EcmFileMovedEvent fileMovedEvent = new EcmFileMovedEvent(source, auth.getName(), ipAddress);
        fileMovedEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(fileMovedEvent);
    }

    public void publishFileMovedInRecycleBinEvent(EcmFile source, Authentication auth, String ipAddress, boolean succeeded)
    {

        log.debug("Publishing a file moved in Recycle Bin event.");
        EcmFileMovedEvent fileMovedEvent = new EcmFileMovedEvent(source, auth.getName(), ipAddress);
        fileMovedEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(fileMovedEvent);
    }


    public void publishFileRenamedEvent(EcmFile source, Authentication auth, String ipAddress, boolean succeeded)
    {

        log.debug("Publishing a file renamed event.");
        EcmFileRenamedEvent fileRenamedEvent = new EcmFileRenamedEvent(source, auth.getName(), ipAddress);
        fileRenamedEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(fileRenamedEvent);
    }

    public void publishFileReplacedEvent(EcmFile source, EcmFileVersion previousActiveFileVersion, Authentication auth, String ipAddress,
            boolean succeeded)
    {

        log.debug("Publishing a file replaced event.");
        EcmFileReplacedEvent fileReplacedEvent = new EcmFileReplacedEvent(source, previousActiveFileVersion, auth.getName(), ipAddress);
        fileReplacedEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(fileReplacedEvent);
    }

    public void publishFileActiveVersionSetEvent(EcmFile source, Authentication auth, String ipAddress, boolean succeeded)
    {

        log.debug("Publishing a file active version set event.");
        String userId = null;
        if (auth != null)
        {
            userId = auth.getName();
        }
        else
        {
            log.warn("Authentication not set, using null for user");
        }
        EcmFileActiveVersionSetEvent fileActiveVersionSetEvent = new EcmFileActiveVersionSetEvent(source, userId, ipAddress);
        fileActiveVersionSetEvent.setSucceeded(succeeded);
        eventPublisher.publishEvent(fileActiveVersionSetEvent);
    }

    public void publishFileUpdatedEvent(EcmFile source, Authentication auth, boolean succeeded)
    {

        log.debug("Publishing a file updated event.");
        EcmFileUpdatedEvent fileUpdatedEvent = new EcmFileUpdatedEvent(source, auth);
        if (auth.getDetails() != null && auth.getDetails() instanceof AcmAuthenticationDetails)
        {
            fileUpdatedEvent.setIpAddress(((AcmAuthenticationDetails) auth.getDetails()).getRemoteAddress());
        }
        fileUpdatedEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(fileUpdatedEvent);
    }

    public void publishFileCopiedAsLinkEvent(EcmFile source, EcmFile original, Authentication auth, String ipAddress, boolean succeeded)
    {

        log.debug("Publishing a file copied as link event.");
        EcmFileCopiedAsLinkEvent filedCopiedAsLinkEvent = new EcmFileCopiedAsLinkEvent(source, original, auth.getName(), ipAddress);
        filedCopiedAsLinkEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(filedCopiedAsLinkEvent);
    }
}
