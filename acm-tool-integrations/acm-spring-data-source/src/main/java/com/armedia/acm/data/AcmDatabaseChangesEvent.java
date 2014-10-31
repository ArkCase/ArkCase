package com.armedia.acm.data;

import org.springframework.context.ApplicationEvent;

/**
 * Created by armdev on 10/21/14.
 */
public class AcmDatabaseChangesEvent extends ApplicationEvent
{
    private AcmObjectChangelist objectChangelist;

    public AcmDatabaseChangesEvent(AcmObjectChangelist source)
    {
        super(source);
        objectChangelist = source;
    }

    public AcmObjectChangelist getObjectChangelist()
    {
        return objectChangelist;
    }
}
