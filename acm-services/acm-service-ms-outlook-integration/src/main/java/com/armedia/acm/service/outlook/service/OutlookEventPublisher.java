package com.armedia.acm.service.outlook.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import com.armedia.acm.service.outlook.model.CalendarEventAddedEvent;
import com.armedia.acm.service.outlook.model.OutlookCalendarItem;

public class OutlookEventPublisher implements ApplicationEventPublisherAware
{
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        eventPublisher = applicationEventPublisher;
        
    }
    
    public void publishCalendarEventAdded(OutlookCalendarItem source, String userId, Long objectId, String objectType){
        CalendarEventAddedEvent event = new CalendarEventAddedEvent(source, userId, objectId, objectType);
        event.setSucceeded(true);
        eventPublisher.publishEvent(event);
    }

}
