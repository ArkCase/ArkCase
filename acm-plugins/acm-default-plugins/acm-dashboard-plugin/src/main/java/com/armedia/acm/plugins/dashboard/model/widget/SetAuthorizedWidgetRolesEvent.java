package com.armedia.acm.plugins.dashboard.model.widget;

import com.armedia.acm.event.AcmEvent;

import java.util.Date;
import java.util.List;

/**
 * Created by marjan.stefanoski on 10/7/2014.
 */
public class SetAuthorizedWidgetRolesEvent extends AcmEvent {
    private static final String EVENT_TYPE = "com.armedia.acm.dashboard.widget.setAuthorizedRoles";

    private static final String OBJECT_TYPE = "AUTHORIZED_ROLES_BY_WIDGET_LIST";

    public SetAuthorizedWidgetRolesEvent(RolesGroupByWidgetDto source) {

        super(source);
        setEventType(EVENT_TYPE);
        setEventDate(new Date());
        setObjectType(OBJECT_TYPE);
    }
}
