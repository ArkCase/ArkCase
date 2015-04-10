package com.armedia.acm.plugins.ecm.service;

import com.armedia.acm.auth.AcmAuthenticationDetails;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;


public class FolderEventPublisher implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher eventPublisher;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void setApplicationEventPublisher( ApplicationEventPublisher applicationEventPublisher ) {
        eventPublisher = applicationEventPublisher;
    }

    public void publishFolderCreatedEvent( AcmFolder source, Authentication auth, String ipAddress,boolean succeeded ) {
        if ( log.isDebugEnabled() ) {
            log.debug("Publishing a folder created event.");
        }
        AcmFolderCreatedEvent folderCreatedEvent = new AcmFolderCreatedEvent(source, auth.getName(),ipAddress);
        if ( auth.getDetails() != null && auth.getDetails() instanceof AcmAuthenticationDetails) {
            folderCreatedEvent.setIpAddress(((AcmAuthenticationDetails) auth.getDetails()).getRemoteAddress());
        }
        folderCreatedEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(folderCreatedEvent);
    }

    public void publishFolderDeletedEvent( AcmFolder source, Authentication auth, String ipAddress,boolean succeeded ) {
        if ( log.isDebugEnabled() ) {
            log.debug("Publishing a folder deleted event.");
        }
        AcmFolderDeletedEvent folderDeletedEvent = new AcmFolderDeletedEvent(source,auth.getName(),ipAddress);
        folderDeletedEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(folderDeletedEvent);
    }


    public void publishFolderRenamedEvent( AcmFolder source, Authentication auth, String ipAddress,boolean succeeded ) {
        if ( log.isDebugEnabled() ) {
            log.debug("Publishing a folder renamed event.");
        }
        AcmFolderRenamedEvent folderRenamedEvent = new AcmFolderRenamedEvent(source,auth.getName(),ipAddress);
        folderRenamedEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(folderRenamedEvent);
    }

}
