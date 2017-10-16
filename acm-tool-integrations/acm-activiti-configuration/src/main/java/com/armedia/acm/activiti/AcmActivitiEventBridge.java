package com.armedia.acm.activiti;

import com.armedia.acm.activiti.model.SpringActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

/**
 * Catch Activiti events, and raise them as Spring events.
 */
public class AcmActivitiEventBridge implements ActivitiEventListener, ApplicationEventPublisherAware
{
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void onEvent(ActivitiEvent activitiEvent)
    {
        ApplicationEvent springEvent = new SpringActivitiEvent(activitiEvent);
        applicationEventPublisher.publishEvent(springEvent);
    }

    @Override
    public boolean isFailOnException()
    {
        return false;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
