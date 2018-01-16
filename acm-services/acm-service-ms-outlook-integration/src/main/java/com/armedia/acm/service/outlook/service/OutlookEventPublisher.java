package com.armedia.acm.service.outlook.service;

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileEmailedEvent;
import com.armedia.acm.service.outlook.model.CalendarEventAddedEvent;
import com.armedia.acm.service.outlook.model.OutlookCalendarItem;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;

public class OutlookEventPublisher implements ApplicationEventPublisherAware
{
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        eventPublisher = applicationEventPublisher;

    }

    public void publishCalendarEventAdded(OutlookCalendarItem source, String userId, Long objectId, String objectType)
    {
        CalendarEventAddedEvent event = new CalendarEventAddedEvent(source, userId, objectId, objectType);
        event.setSucceeded(true);
        eventPublisher.publishEvent(event);
    }

    public void publishFileEmailedEvent(EcmFile emailedFile, Authentication authentication)
    {
        EcmFileEmailedEvent event = new EcmFileEmailedEvent(emailedFile, authentication);
        event.setSucceeded(true);
        eventPublisher.publishEvent(event);
    }
}
