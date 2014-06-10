package com.armedia.acm.plugins.task.model;

/**
 * Created by armdev on 6/2/14.
 */
public class AcmTaskSearchResultEvent extends AcmTaskEvent
{
    public AcmTaskSearchResultEvent(AcmTask source)
    {
        super(source);

        setEventType("com.armedia.acm.plugins.task.search.result");
    }
}
