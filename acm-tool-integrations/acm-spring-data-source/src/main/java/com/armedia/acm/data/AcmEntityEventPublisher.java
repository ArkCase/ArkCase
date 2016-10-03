package com.armedia.acm.data;


import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

public class AcmEntityEventPublisher implements ApplicationEventPublisherAware
{
    private ApplicationEventPublisher applicationEventPublisher;

    public void publishEntityChangedEvent(AcmEntityChangesHolder eventHolder)
    {
        AcmEntityChangeEvent acmEntityUpdatedEvent = new AcmEntityChangeEvent(eventHolder);
        applicationEventPublisher.publishEvent(acmEntityUpdatedEvent);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }

}
