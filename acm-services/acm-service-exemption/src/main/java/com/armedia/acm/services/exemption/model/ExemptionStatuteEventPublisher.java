package com.armedia.acm.services.exemption.model;

import com.armedia.acm.auth.AuthenticationUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

public class ExemptionStatuteEventPublisher implements ApplicationEventPublisherAware
{
    private ApplicationEventPublisher applicationEventPublisher;

    public void publishExemptionStatuteCreatedEvent(ExemptionStatute source)
    {
        ExemptionStatuteCreatedEvent exemptionStatuteCreatedEvent = new ExemptionStatuteCreatedEvent(source);
        exemptionStatuteCreatedEvent.setUserId(AuthenticationUtils.getUsername());
        exemptionStatuteCreatedEvent.setIpAddress(AuthenticationUtils.getUserIpAddress());
        exemptionStatuteCreatedEvent.setParentObjectId(source.getParentObjectId());
        exemptionStatuteCreatedEvent.setParentObjectType(source.getParentObjectType());
        exemptionStatuteCreatedEvent.setSucceeded(true);
        getApplicationEventPublisher().publishEvent(exemptionStatuteCreatedEvent);
    }

    public void publishExemptionStatuteDeletedEvent(ExemptionStatute source)
    {
        ExemptionStatuteDeletedEvent exemptionStatuteDeletedEvent = new ExemptionStatuteDeletedEvent(source);
        exemptionStatuteDeletedEvent.setUserId(AuthenticationUtils.getUsername());
        exemptionStatuteDeletedEvent.setIpAddress(AuthenticationUtils.getUserIpAddress());
        exemptionStatuteDeletedEvent.setParentObjectId(source.getParentObjectId());
        exemptionStatuteDeletedEvent.setParentObjectType(source.getParentObjectType());
        exemptionStatuteDeletedEvent.setSucceeded(true);
        getApplicationEventPublisher().publishEvent(exemptionStatuteDeletedEvent);
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
