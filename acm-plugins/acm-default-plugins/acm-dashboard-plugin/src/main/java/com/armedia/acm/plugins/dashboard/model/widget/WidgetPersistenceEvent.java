package com.armedia.acm.plugins.dashboard.model.widget;

import com.armedia.acm.event.AcmEvent;

import java.util.Date;

/**
 * Created by marjan.stefanoski on 9/20/2014.
 */
public class WidgetPersistenceEvent extends AcmEvent {
    private static final String OBJECT_TYPE = "WIDGET";

    public WidgetPersistenceEvent(Widget source) {
        super(source);
        setObjectId(source.getWidgetId());
        setEventDate(new Date());
    }

    @Override
    public String getObjectType()
    {
        return OBJECT_TYPE;
    }

}
