package com.armedia.acm.activiti;

import com.armedia.acm.event.AcmEvent;
import org.activiti.engine.runtime.ProcessInstance;

import java.util.Date;
import java.util.Map;

/**
 * Created by armdev on 5/30/14.
 */
public class AcmBusinessProcessEvent extends AcmEvent
{
    private ProcessInstance source;
    private Map<String, Object> processVariables;

    public AcmBusinessProcessEvent(ProcessInstance source)
    {
        super(source);

        setSucceeded(true);
        setObjectType("BUSINESS_PROCESS");
        setObjectId(Long.valueOf(source.getProcessInstanceId()));
        setEventDate(new Date());
        setUserId("ACTIVITI_SYSTEM");
        setSource(source);
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
