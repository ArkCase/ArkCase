package com.armedia.acm.plugins.task.model;

/**
 * Created by armdev on 6/3/14.
 */
public class AcmTaskCompletedEvent extends AcmTaskEvent
{
    public AcmTaskCompletedEvent(AcmTask source)
    {
        super(source);

        setEventType("com.armedia.acm.plugins.task.completed");
    }
}
