package com.armedia.acm.plugins.task.model;

/**
 * Created by armdev on 6/3/14.
 */
public class AcmAdHocTaskCreatedEvent extends AcmTaskEvent
{
    public AcmAdHocTaskCreatedEvent(AcmTask source)
    {
        super(source);
        setEventType("com.armedia.acm.plugins.task.adHocTaskCreated");
    }
}
