package com.armedia.acm.plugins.casefile.service;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dmiller on 2/19/16.
 */
public class TestActivitiEventListener implements ActivitiEventListener
{
    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void onEvent(ActivitiEvent activitiEvent)
    {
        log.info("Received event {}", activitiEvent.getType());
    }

    @Override
    public boolean isFailOnException()
    {
        return false;
    }
}
