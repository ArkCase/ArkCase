package com.armedia.acm.plugins.task.model;

/**
 * Created by armdev on 6/4/14.
 */
public class AcmFindTaskByIdEvent extends AcmTaskEvent
{
    public AcmFindTaskByIdEvent(AcmTask source)
    {
        super(source);

        setEventType("com.armedia.acm.plugins.task.findById");
    }
}
