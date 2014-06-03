package com.armedia.acm.plugins.task.model;

import com.armedia.acm.event.AcmEvent;

import java.util.Date;

/**
 * Created by armdev on 6/2/14.
 */
public class AcmTaskSearchResultEvent extends AcmEvent
{
    public AcmTaskSearchResultEvent(AcmTask source)
    {
        super(source);

        setObjectId(source.getTaskId());
        setObjectType("TASK");
        setEventDate(new Date());
        setSucceeded(true);
        setEventType("com.armedia.acm.plugins.task.search.result");
    }
}
