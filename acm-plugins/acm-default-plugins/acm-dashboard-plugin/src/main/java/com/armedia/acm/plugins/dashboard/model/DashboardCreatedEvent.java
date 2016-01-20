package com.armedia.acm.plugins.dashboard.model;

/**
 * Created by marst on 7/31/14.
 */
public class DashboardCreatedEvent extends DashboardPersistenceEvent
{

    public DashboardCreatedEvent(Dashboard source)
    {
        super(source);
    }

    @Override
    public String getEventType()
    {
        return DashboardConstants.EVENT_TYPE_DASHBOARD_CREATED;
    }
}
