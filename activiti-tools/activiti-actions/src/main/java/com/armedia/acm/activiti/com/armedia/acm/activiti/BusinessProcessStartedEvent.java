package com.armedia.acm.activiti.com.armedia.acm.activiti;

import com.armedia.acm.event.AcmEvent;
import org.activiti.engine.runtime.ProcessInstance;

import java.util.Date;

/**
 * Created by armdev on 4/16/14.
 */
public class BusinessProcessStartedEvent extends AcmEvent
{

    private final static String EVENT_TYPE = "com.armedia.acm.businessprocess.started";

    public BusinessProcessStartedEvent(ProcessInstance source)
    {
        super(source);
        setEventDate(new Date());
        setEventType(EVENT_TYPE);
        setObjectId(Long.valueOf(source.getProcessInstanceId()));
    }
}
