package com.armedia.acm.activiti;

import com.armedia.acm.activiti.com.armedia.acm.activiti.BusinessProcessStartedEvent;
import com.armedia.acm.event.AcmEvent;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.mule.api.annotations.param.InboundHeaders;
import org.mule.api.annotations.param.Payload;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by armdev on 4/16/14.
 */
public class StartBusinessProcess implements ApplicationEventPublisherAware
{
    private ApplicationEventPublisher applicationEventPublisher;

    private RuntimeService runtimeService;

    public void startBusinessProcess(
            @Payload AcmEvent acmEvent,
            @InboundHeaders("*") Map<String, Object> muleHeaders)
    {
        String businessProcessKey = (String) muleHeaders.get("processDefinitionKey");
        if ( businessProcessKey == null )
        {
            throw new IllegalStateException("Must specify a processDefinitionKey to start a business process.");
        }

        Map<String, Object> messageHeaders = filterMuleAndJmsHeaders(muleHeaders);

        ProcessInstance pi = getRuntimeService().startProcessInstanceByKey(businessProcessKey, messageHeaders);

        BusinessProcessStartedEvent event = new BusinessProcessStartedEvent(pi);
        event.setSucceeded(true);
        event.setIpAddress((String) muleHeaders.get("IP_ADDRESS"));
        event.setUserId((String) muleHeaders.get("ACM_USER"));
        event.setObjectType(businessProcessKey);
        getApplicationEventPublisher().publishEvent(event);

    }

    private Map<String, Object> filterMuleAndJmsHeaders(Map<String, Object> muleHeaders)
    {
        // exclude Mule and JMS headers
        Map<String, Object> messageHeaders = new HashMap<>();
        for ( Map.Entry<String, Object> header : muleHeaders.entrySet() )
        {
            if ( !header.getKey().startsWith("MULE_") && !header.getKey().startsWith("JMS") )
            {
                messageHeaders.put(header.getKey(), header.getValue());
            }
        }
        return messageHeaders;
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
