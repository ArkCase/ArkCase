package com.armedia.acm.plugins.dashboard.model;

import com.armedia.acm.event.AcmEvent;
import java.util.Date;

/**
 * Created by marst on 7/31/14.
 */
public class GetDashbordByUserIdEvent extends AcmEvent {

    private static final String EVENT_TYPE = "com.armedia.acm.dashboard.getByUserId";

    private static final String OBJECT_TYPE = "DASHBOARD";

    public GetDashbordByUserIdEvent(Dashboard source) {

        super(source);
        setEventType(EVENT_TYPE);
        setObjectId(source.getDashboardId());
        setEventDate(new Date());
        setObjectType(OBJECT_TYPE);
    }

}
