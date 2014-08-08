package com.armedia.acm.plugins.dashboard.model;

/**
 * Created by marst on 7/31/14.
 */
public class DashboardUpdatedEvent extends DashboardPersistenceEvent {

    private static final String EVENT_TYPE = "com.armedia.acm.dashboard.updated";

    public DashboardUpdatedEvent(Dashboard source)
    {
        super(source);
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }

}
