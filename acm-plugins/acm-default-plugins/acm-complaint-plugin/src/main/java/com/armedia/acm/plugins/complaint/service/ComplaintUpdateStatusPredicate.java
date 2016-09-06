package com.armedia.acm.plugins.complaint.service;

import com.armedia.acm.activiti.AcmBusinessProcessEvent;

import org.apache.commons.collections.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by armdev on 6/5/14.
 */
public class ComplaintUpdateStatusPredicate implements Predicate
{
    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public boolean evaluate(Object object)
    {
        if (log.isDebugEnabled())
        {
            log.debug("type of event: " + object.getClass().toString());
        }

        if (!(object instanceof AcmBusinessProcessEvent))
        {
            return false;
        }

        AcmBusinessProcessEvent event = (AcmBusinessProcessEvent) object;

        return shouldComplaintStatusBeUpdated(event);
    }

    public boolean shouldComplaintStatusBeUpdated(AcmBusinessProcessEvent event)
    {
        Map<String, Object> processVariables = event.getProcessVariables();

        if (log.isDebugEnabled())
        {
            log.debug("# of process variables: " + event.getProcessVariables().size());
            processVariables.forEach((key, value) -> log.debug("pvar - {} = {}", key, value));
        }
        return "COMPLAINT".equals(processVariables.get("OBJECT_TYPE"))
                && "cmComplaintWorkflow".equals(processVariables.get("processDefinitionKey"));
    }

}
