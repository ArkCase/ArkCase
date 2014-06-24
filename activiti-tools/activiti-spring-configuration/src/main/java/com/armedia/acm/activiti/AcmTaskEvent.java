package com.armedia.acm.activiti;

import com.armedia.acm.event.AcmEvent;
import org.activiti.engine.task.Task;

import java.util.Date;

/**
 * Created by armdev on 6/24/14.
 */
public class AcmTaskEvent extends AcmEvent
{
    public AcmTaskEvent(Task source, String taskEvent)
    {
        super(source);
        setSucceeded(true);
        setObjectType("TASK");
        setEventType("com.armedia.acm.activiti.task." + taskEvent);
        setObjectId(Long.valueOf(source.getId()));
        setEventDate(new Date());
        setUserId("ACTIVITI_SYSTEM");
    }
}
