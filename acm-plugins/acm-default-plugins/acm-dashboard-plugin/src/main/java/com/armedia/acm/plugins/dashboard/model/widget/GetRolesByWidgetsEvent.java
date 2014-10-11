package com.armedia.acm.plugins.dashboard.model.widget;

import com.armedia.acm.event.AcmEvent;

import java.util.Date;
import java.util.List;

/**
 * Created by marjan.stefanoski on 9/30/2014.
 */
public class GetRolesByWidgetsEvent extends AcmEvent {
    private static final String EVENT_TYPE = "com.armedia.acm.dashboard.widget.getRolesByWidgets";

    private static final String OBJECT_TYPE = "ROLES_BY_WIDGET_LIST";

    public GetRolesByWidgetsEvent(List<RolesGroupByWidgetDto> source) {

        super(source);
        setEventType(EVENT_TYPE);
        setEventDate(new Date());
        setObjectType(OBJECT_TYPE);
    }
}
