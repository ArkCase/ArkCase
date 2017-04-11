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

    public void publishEvent(DocumentRepositoryEvent event)
    {
        eventPublisher.publishEvent(event);
    }

    public void publishSearchedEvent(DocumentRepository source, boolean succeeded)
    {
        log.debug("Publishing a Document Repository viewed event.");
        DocumentRepositoryEvent event = new DocumentRepositoryEvent(source, "viewed");
        event.setSucceeded(succeeded);
        event.setIpAddress(AuthenticationUtils.getUserIpAddress());
        eventPublisher.publishEvent(event);
    }
}
