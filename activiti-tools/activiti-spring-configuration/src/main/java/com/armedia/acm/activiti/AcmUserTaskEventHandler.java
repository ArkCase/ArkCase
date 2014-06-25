package com.armedia.acm.activiti;

import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

/**
 * Created by armdev on 6/24/14.
 */
public class AcmUserTaskEventHandler implements ApplicationEventPublisherAware
{
    private ApplicationEventPublisher applicationEventPublisher;
    private Logger log = LoggerFactory.getLogger(getClass());

    public void handleTaskEvent(String eventName, Task task)
    {
        if ( log.isDebugEnabled() )
        {
            log.debug("Got a task event '" + eventName + "'; execution of type'" + task.getClass().getName() + "'");
        }

        AcmTaskEvent event = new AcmTaskEvent(task, eventName);

        getApplicationEventPublisher().publishEvent(event);

    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public ApplicationEventPublisher getApplicationEventPublisher()
    {
        return applicationEventPublisher;
    }
}
