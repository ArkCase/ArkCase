package com.armedia.acm.activiti.model;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.springframework.context.ApplicationEvent;

/**
 * Created by dmiller on 12/6/2016.
 */
public class SpringActivitiEvent extends ApplicationEvent
{

    public SpringActivitiEvent(ActivitiEvent source)
    {
        super(source);
    }
}
