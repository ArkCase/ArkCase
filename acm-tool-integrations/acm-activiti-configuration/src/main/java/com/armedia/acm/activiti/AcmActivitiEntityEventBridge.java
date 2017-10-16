package com.armedia.acm.activiti;

import com.armedia.acm.activiti.model.SpringActivitiEntityEvent;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.BaseEntityEventListener;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

/**
 * Created by dmiller on 12/6/2016.
 */
public class AcmActivitiEntityEventBridge extends BaseEntityEventListener implements ApplicationEventPublisherAware
{
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    protected void onCreate(ActivitiEvent event)
    {
        SpringActivitiEntityEvent springActivitiEntityEvent = new SpringActivitiEntityEvent("create", event);
        applicationEventPublisher.publishEvent(springActivitiEntityEvent);
    }

    @Override
    protected void onDelete(ActivitiEvent event)
    {
        SpringActivitiEntityEvent springActivitiEntityEvent = new SpringActivitiEntityEvent("delete", event);
        applicationEventPublisher.publishEvent(springActivitiEntityEvent);
    }

    @Override
    protected void onUpdate(ActivitiEvent event)
    {
        SpringActivitiEntityEvent springActivitiEntityEvent = new SpringActivitiEntityEvent("update", event);
        applicationEventPublisher.publishEvent(springActivitiEntityEvent);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
