package com.armedia.acm.plugins.dashboard.model.widget;

/**
 * Created by marjan.stefanoski on 9/22/2014.
 */
public class WidgetRoleCreatedEvent extends WidgetRolePersistenceEvent  {

    private static final String EVENT_TYPE = "com.armedia.acm.widgetrole.created";

    public WidgetRoleCreatedEvent(WidgetRole source) {
        super(source);
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}
