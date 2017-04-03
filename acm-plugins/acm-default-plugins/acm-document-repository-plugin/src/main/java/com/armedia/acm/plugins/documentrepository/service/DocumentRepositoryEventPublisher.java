package com.armedia.acm.plugins.documentrepository.service;

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.plugins.documentrepository.model.DocumentRepository;
import com.armedia.acm.plugins.documentrepository.model.DocumentRepositoryEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;


public class DocumentRepositoryEventPublisher implements ApplicationEventPublisherAware
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        eventPublisher = applicationEventPublisher;
    }

    public void publishCreatedEvent(DocumentRepository source, boolean succeeded)
    {
        log.debug("Publishing a Document Repository created event.");
        DocumentRepositoryEvent event = new DocumentRepositoryEvent(source, "created");
        event.setSucceeded(succeeded);
        event.setIpAddress(AuthenticationUtils.getUserIpAddress());
        eventPublisher.publishEvent(event);
    }

    public void publishUpdatedEvent(DocumentRepository source, boolean succeeded)
    {
        log.debug("Publishing a Document Repository updated event.");
        DocumentRepositoryEvent event = new DocumentRepositoryEvent(source, "updated");
        event.setSucceeded(succeeded);
        event.setIpAddress(AuthenticationUtils.getUserIpAddress());
        eventPublisher.publishEvent(event);
    }

}
