package com.armedia.acm.plugins.dashboard.model.widget;

import com.armedia.acm.event.AcmEvent;
import com.armedia.acm.plugins.dashboard.model.Dashboard;

import java.util.Date;
import java.util.List;

/**
 * Created by marjan.stefanoski on 9/20/2014.
 */
public class GetWidgetsByUserRolesEvent extends AcmEvent {
    private static final String EVENT_TYPE = "com.armedia.acm.dashboard.widget.getByUserRoles";

    private static final String OBJECT_TYPE = "WIDGET_LIST";

    public GetWidgetsByUserRolesEvent(List<Widget> source) {

        super(source);
        setEventType(EVENT_TYPE);
        setEventDate(new Date());
        setObjectType(OBJECT_TYPE);
    }
}
