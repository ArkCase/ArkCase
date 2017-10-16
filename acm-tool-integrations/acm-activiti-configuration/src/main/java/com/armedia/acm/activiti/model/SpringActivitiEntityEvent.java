package com.armedia.acm.activiti.model;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.springframework.context.ApplicationEvent;

/**
 * Created by dmiller on 12/6/2016.
 */
public class SpringActivitiEntityEvent extends ApplicationEvent
{
    private String eventType;

    public SpringActivitiEntityEvent(String eventType, ActivitiEvent event)
    {
        super(event);
        this.eventType = eventType;
    }

    public String getEventType()
    {
        return eventType;
    }
}
