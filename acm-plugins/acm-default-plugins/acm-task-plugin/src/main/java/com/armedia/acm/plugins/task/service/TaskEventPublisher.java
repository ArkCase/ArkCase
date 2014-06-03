package com.armedia.acm.plugins.task.service;

import com.armedia.acm.plugins.task.model.AcmTaskSearchResultEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;

/**
 * Created by armdev on 6/2/14.
 */
public class TaskEventPublisher implements ApplicationEventPublisherAware
{
    private ApplicationEventPublisher applicationEventPublisher;

    public void publishTaskSearchResultEvent(
            AcmTaskSearchResultEvent event,
            Authentication authentication,
            String ipAddress)
    {
        if ( authentication != null )
        {
            event.setUserId(authentication.getName());
        }

        event.setIpAddress(ipAddress);

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
