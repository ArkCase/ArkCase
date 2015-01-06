package com.armedia.acm.data;

import org.springframework.context.ApplicationEvent;

/**
 * Created by armdev on 10/21/14.
 */
public class AcmDatabasePreCommitEvent extends ApplicationEvent
{
    private final Object source;
    private final String eventType;

    public AcmDatabasePreCommitEvent(Object source, String eventType)
    {
        super(source);
        this.source = source;
        this.eventType = eventType;
    }

    @Override
    public Object getSource()
    {
        return source;
    }

    public String getEventType()
    {
        return eventType;
    }
}
