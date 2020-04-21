package com.armedia.acm.services.exemption.model;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.plugins.ecm.model.EcmFile;

/**
 * Created by ana.serafimoska
 */
public class DocumentRedactionEventPublisher implements ApplicationEventPublisherAware
{
    private ApplicationEventPublisher applicationEventPublisher;

    public void publishExemptionCodeCreatedEvent(EcmFile source)
    {
        DocumentCodeCreatedEvent documentCodeCreatedEvent = new DocumentCodeCreatedEvent(source);
        documentCodeCreatedEvent.setUserId(AuthenticationUtils.getUsername());
        documentCodeCreatedEvent.setIpAddress(AuthenticationUtils.getUserIpAddress());
        documentCodeCreatedEvent.setParentObjectId(source.getParentObjectId());
        documentCodeCreatedEvent.setParentObjectType(source.getParentObjectType());
        documentCodeCreatedEvent.setSucceeded(true);
        getApplicationEventPublisher().publishEvent(documentCodeCreatedEvent);
    }

    public void publishExemptionCodeDeletedEvent(EcmFile source)
    {
        DocumentCodeDeletedEvent documentCodeDeletedEvent = new DocumentCodeDeletedEvent(source);
        documentCodeDeletedEvent.setUserId(AuthenticationUtils.getUsername());
        documentCodeDeletedEvent.setIpAddress(AuthenticationUtils.getUserIpAddress());
        documentCodeDeletedEvent.setParentObjectId(source.getParentObjectId());
        documentCodeDeletedEvent.setParentObjectType(source.getParentObjectType());
        documentCodeDeletedEvent.setSucceeded(true);
        getApplicationEventPublisher().publishEvent(documentCodeDeletedEvent);

    }

    public ApplicationEventPublisher getApplicationEventPublisher()
    {
        return applicationEventPublisher;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
