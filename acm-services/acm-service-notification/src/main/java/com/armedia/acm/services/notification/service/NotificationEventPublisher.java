package com.armedia.acm.services.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

public class NotificationEventPublisher  implements ApplicationEventPublisherAware
{
    private ApplicationEventPublisher applicationEventPublisher;
    
    private transient final Logger log = LoggerFactory.getLogger(getClass());
    
    public void publishNotificationEvent(ApplicationNotificationEvent event)
    {
        getApplicationEventPublisher().publishEvent(event);
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
