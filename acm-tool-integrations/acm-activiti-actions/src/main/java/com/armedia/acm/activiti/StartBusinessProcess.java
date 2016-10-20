package com.armedia.acm.activiti;

import com.armedia.acm.core.model.AcmEvent;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.mule.api.annotations.expressions.Lookup;
import org.mule.api.annotations.param.InboundHeaders;
import org.mule.api.annotations.param.Payload;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by armdev on 4/16/14.
 */
public class StartBusinessProcess implements ApplicationEventPublisherAware
{
    private ApplicationEventPublisher applicationEventPublisher;

    public void startBusinessProcess(@Payload AcmEvent acmEvent, @InboundHeaders("*") Map<String, Object> muleHeaders,
            @Lookup("arkContext") ApplicationContext arkContext)
    {

        Boolean eventWasSuccessful = (Boolean) muleHeaders.get("EVENT_SUCCEEDED");

        // only launch the process if the parent object was actually created...
        if (!eventWasSuccessful.booleanValue())
        {
            return;
        }

        String businessProcessKey = (String) muleHeaders.get("processDefinitionKey");
        if (businessProcessKey == null)
        {
            throw new IllegalStateException("Must specify a processDefinitionKey to start a business process.");
        }

        Map<String, Object> messageHeaders = filterMuleAndJmsHeaders(muleHeaders);

        RuntimeService runtimeService = arkContext.getBean("activitiRuntimeService", RuntimeService.class);

        ProcessInstance pi = runtimeService.startProcessInstanceByKey(businessProcessKey, messageHeaders);

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
        return muleHeaders.entrySet().stream().filter(header ->
        {
            return !header.getKey().startsWith("MULE_") && !header.getKey().startsWith("JMS")
                    && !header.getKey().equals("activitiRuntimeService");
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
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
