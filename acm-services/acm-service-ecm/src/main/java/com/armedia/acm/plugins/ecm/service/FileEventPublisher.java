package com.armedia.acm.plugins.ecm.service;

import com.armedia.acm.auth.AcmAuthenticationDetails;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;


public class FileEventPublisher implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher eventPublisher;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void setApplicationEventPublisher( ApplicationEventPublisher applicationEventPublisher ) {
        eventPublisher = applicationEventPublisher;
    }

    public void publishFileCreatedEvent( EcmFile source, Authentication auth, String ipAddress, boolean succeeded ) {
        if ( log.isDebugEnabled() ) {
            log.debug("Publishing a file created event.");
        }
        EcmFileCreatedEvent fileCreatedEvent = new EcmFileCreatedEvent(source, auth.getName(), ipAddress);
        if ( auth.getDetails() != null && auth.getDetails() instanceof AcmAuthenticationDetails) {
            fileCreatedEvent.setIpAddress(((AcmAuthenticationDetails) auth.getDetails()).getRemoteAddress());
        }
        fileCreatedEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(fileCreatedEvent);
    }

    public void publishFileDeletedEvent( EcmFile source, Authentication auth, String ipAddress, boolean succeeded ) {
        if ( log.isDebugEnabled() ) {
            log.debug("Publishing a file deleted event.");
        }
        EcmFileDeletedEvent fileDeletedEvent = new EcmFileDeletedEvent(source,auth.getName(), ipAddress);
        fileDeletedEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(fileDeletedEvent);
    }

    public void publishFileCopiedEvent( EcmFile source, Authentication auth, String ipAddress, boolean succeeded ) {
        if ( log.isDebugEnabled() ) {
            log.debug("Publishing a file copied event.");
        }
        EcmFiledCopiedEvent filedCopiedEvent = new EcmFiledCopiedEvent(source,auth.getName(),ipAddress);
        filedCopiedEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(filedCopiedEvent);
    }

    public void publishFileMovedEvent( EcmFile source, Authentication auth, String ipAddress, boolean succeeded ) {
        if ( log.isDebugEnabled() ) {
            log.debug("Publishing a file moved event.");
        }
        EcmFileMovedEvent fileMovedEvent = new EcmFileMovedEvent(source,auth.getName(), ipAddress);
        fileMovedEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(fileMovedEvent);
    }

    public void publishFileRenamedEvent( EcmFile source, Authentication auth, String ipAddress, boolean succeeded ) {
        if ( log.isDebugEnabled() ) {
            log.debug("Publishing a file renamed event.");
        }
        EcmFileRenamedEvent fileRenamedEvent = new EcmFileRenamedEvent(source,auth.getName(),ipAddress);
        fileRenamedEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(fileRenamedEvent);
    }

    public void publishFileReplacedEvent( EcmFile source, Authentication auth, String ipAddress, boolean succeeded ) {
        if ( log.isDebugEnabled() ) {
            log.debug("Publishing a file replaced event.");
        }
        EcmFileReplacedEvent fileReplacedEvent = new EcmFileReplacedEvent(source,auth.getName(),ipAddress);
        fileReplacedEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(fileReplacedEvent);
    }

}
