package com.armedia.acm.plugins.dashboard.model.widget;

/**
 * Created by marjan.stefanoski on 9/20/2014.
 */
public class WidgetUpdatedEvent extends WidgetPersistenceEvent{

    private static final String EVENT_TYPE = "com.armedia.acm.widget.update";

    public WidgetUpdatedEvent(Widget source)
    {
        super(source);
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}
