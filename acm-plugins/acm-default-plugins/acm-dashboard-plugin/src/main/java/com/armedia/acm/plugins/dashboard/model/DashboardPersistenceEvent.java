package com.armedia.acm.plugins.dashboard.model;

import com.armedia.acm.event.AcmEvent;

import java.util.Date;

/**
 * Created by marst on 7/31/14.
 */

public class DashboardPersistenceEvent extends AcmEvent {

    private static final String OBJECT_TYPE = "DASHBOARD";

    public DashboardPersistenceEvent(Dashboard source) {
        super(source);
        setObjectId(source.getDashboardId());
        setEventDate(new Date());
        setUserId(source.getDashboardOwner().getUserId());
    }

    @Override
    public String getObjectType()
    {
        return OBJECT_TYPE;
    }

}
