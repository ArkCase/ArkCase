package com.armedia.acm.plugins.ecm.service;

import com.armedia.acm.auth.AcmAuthenticationDetails;
import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.event.AcmFolderCopiedEvent;
import com.armedia.acm.plugins.ecm.model.event.AcmFolderCreatedEvent;
import com.armedia.acm.plugins.ecm.model.event.AcmFolderDeletedEvent;
import com.armedia.acm.plugins.ecm.model.event.AcmFolderMovedEvent;
import com.armedia.acm.plugins.ecm.model.event.AcmFolderRenamedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;


public class FolderEventPublisher implements ApplicationEventPublisherAware
{

    private transient final Logger log = LoggerFactory.getLogger(getClass());
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

}
