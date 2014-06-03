package com.armedia.acm.activiti;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import java.util.Map;

/**
 * Created by armdev on 5/30/14.
 */
public class ActivitiProcessEventHandler implements ApplicationEventPublisherAware
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private RuntimeService runtimeService;
    private ApplicationEventPublisher applicationEventPublisher;

    // IDEA says this method is not called; but actually it is called from Activiti.  Do NOT delete this method.
    public void handleProcessEvent(String eventName, ProcessInstance execution)
    {
        log.info("Got an Activiti event; eventName: " + eventName + "; " +
            "process instance id: " + execution.getProcessInstanceId());

        Map<String, Object> processVariables = getRuntimeService().getVariables(execution.getId());

        AcmBusinessProcessEvent event = new AcmBusinessProcessEvent(execution);
        event.setEventType("com.armedia.acm.activiti.businessProcess." + eventName);
        event.setProcessVariables(processVariables);

        applicationEventPublisher.publishEvent(event);

    }

    public RuntimeService getRuntimeService()
    {
        return runtimeService;
    }

    public void setRuntimeService(RuntimeService runtimeService)
    {
        this.runtimeService = runtimeService;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
