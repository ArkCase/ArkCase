package com.armedia.acm.activiti;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import java.util.Map;

/**
 * Created by armdev on 6/24/14.
 */
public class AcmUserTaskEventHandler implements ApplicationEventPublisherAware
{
    private ApplicationEventPublisher applicationEventPublisher;
    private Logger log = LoggerFactory.getLogger(getClass());
    private RuntimeService runtimeService;

    public void handleTaskEvent(String eventName, Task task)
    {
        log.debug("Got a task event '{}'; execution of type '{}'", eventName, task.getClass().getName());

        Map<String, Object> processVariables = getRuntimeService().getVariables(task.getProcessInstanceId());
        log.debug("process variables from runtime: {}", processVariables.size());

        Map<String, Object> localVariables = getRuntimeService().getVariablesLocal(task.getExecutionId());
        log.debug("local variables from runtime: {}", processVariables.size());

        AcmTaskActivitiEvent event = new AcmTaskActivitiEvent(task, eventName, processVariables, localVariables);

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

    public RuntimeService getRuntimeService()
    {
        return runtimeService;
    }

    public void setRuntimeService(RuntimeService runtimeService)
    {
        this.runtimeService = runtimeService;
    }
}
