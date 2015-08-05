package com.armedia.acm.plugins.task.service;

import com.armedia.acm.core.model.AcmEvent;
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

/**
 * Created by armdev on 6/2/14.
 */
public class TaskEventPublisher implements ApplicationEventPublisherAware
{
    private ApplicationEventPublisher applicationEventPublisher;

    public void publishTaskEvent(AcmApplicationTaskEvent event)
    {
        getApplicationEventPublisher().publishEvent(event);
    }

    public void publishAcmEvent( AcmEvent event ) {
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
