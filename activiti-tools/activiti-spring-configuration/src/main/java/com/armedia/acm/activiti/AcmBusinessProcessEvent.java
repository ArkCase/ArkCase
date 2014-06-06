package com.armedia.acm.activiti;

import com.armedia.acm.event.AcmEvent;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;

/**
 * Created by armdev on 5/30/14.
 */
public class AcmBusinessProcessEvent extends AcmEvent
{
    private ProcessInstance source;
    private Map<String, Object> processVariables;

    private Logger log = LoggerFactory.getLogger(getClass());

    public AcmBusinessProcessEvent(ProcessInstance source)
    {
        super(source);

        setSucceeded(true);
        setObjectType("BUSINESS_PROCESS");
        setObjectId(Long.valueOf(source.getProcessInstanceId()));
        setEventDate(new Date());
        setUserId("ACTIVITI_SYSTEM");
        if ( source.getProcessVariables() != null && source.getProcessVariables().containsKey("IP_ADDRESS"))
        {
            setIpAddress((String) source.getProcessVariables().get("IP_ADDRESS"));
        }
        log.debug("# of process variables: " + source.getProcessVariables().size());
        setEventProperties(source.getProcessVariables());

        // do not put the ProcessInstance in the event source... since
        // the ProcessInstance is not Serializable, so it can't be sent to a
        // JMS queue. //  setSource(source);
    }

    public ProcessInstance getSource()
    {
        return source;
    }

    public void setSource(ProcessInstance source)
    {
        this.source = source;
    }

    public Map<String, Object> getProcessVariables()
    {
        return processVariables;
    }

    public void setProcessVariables(Map<String, Object> processVariables)
    {
        this.processVariables = processVariables;
    }
}
