package com.armedia.acm.plugins.dashboard.model.widget;

import com.armedia.acm.event.AcmEvent;

import java.util.Date;

/**
 * Created by marjan.stefanoski on 9/22/2014.
 */
public class WidgetRolePersistenceEvent extends AcmEvent {
    private static final String OBJECT_TYPE = "WIDGET_ROLE";

    public WidgetRolePersistenceEvent(WidgetRole source) {
        super(source);
        setEventDate(new Date());
    }

    @Override
    public String getObjectType()
    {
        return OBJECT_TYPE;
    }
}
