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
        log.debug("type of event: " + object.getClass().toString());
        if ( ! ( object instanceof AcmBusinessProcessEvent ) )
        {
            return false;
        }

        AcmBusinessProcessEvent event = (AcmBusinessProcessEvent) object;

        return shouldComplaintStatusBeUpdated(event);
    }

    public boolean shouldComplaintStatusBeUpdated(AcmBusinessProcessEvent event)
    {
        Map<String, Object> processVariables = event.getProcessVariables();

        if ( log.isDebugEnabled() )
        {
            log.debug("# of process variables: " + event.getProcessVariables().size());
            for ( Map.Entry<String, Object> pv : processVariables.entrySet() )
            {
                log.debug("pvar - " + pv.getKey() + " = " + pv.getValue());
            }
        }
        return "COMPLAINT".equals(processVariables.get("OBJECT_TYPE")) && "cmComplaintWorkflow".equals(processVariables.get("processDefinitionKey"));
    }


}
