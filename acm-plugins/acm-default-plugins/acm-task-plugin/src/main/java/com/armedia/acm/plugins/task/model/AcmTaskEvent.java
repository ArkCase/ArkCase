package com.armedia.acm.plugins.task.model;

import com.armedia.acm.event.AcmEvent;

import java.util.Date;

/**
 * Created by armdev on 6/3/14.
 */
public abstract class AcmTaskEvent extends AcmEvent
{
    public AcmTaskEvent(AcmTask source)
    {
        super(source);

        setObjectId(source.getTaskId());
        setObjectType("TASK");
        setEventDate(new Date());
        setSucceeded(true);
    }
}
