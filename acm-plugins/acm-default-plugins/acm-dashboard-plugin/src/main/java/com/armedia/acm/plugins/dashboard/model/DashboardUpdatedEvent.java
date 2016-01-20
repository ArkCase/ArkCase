package com.armedia.acm.plugins.dashboard.model;

/**
 * Created by marst on 7/31/14.
 */
public class DashboardUpdatedEvent extends DashboardPersistenceEvent
{

    public DashboardUpdatedEvent(Dashboard source)
    {
        super(source);
    }

    @Override
    public String getEventType()
    {
        return DashboardConstants.EVENT_TYPE_DASHBOARD_UPDATED;
    }
}
