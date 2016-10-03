package com.armedia.acm.plugins.person.model;


import com.armedia.acm.core.model.AcmEvent;

import java.util.Date;

public class PersonAliasEvent extends AcmEvent
{
    private static final String EVENT_TYPE = "com.armedia.acm.personAlias";

    private String eventAction;

    public PersonAliasEvent(PersonAlias source, String ipAddress)
    {
        super(source);
        setEventDate(new Date());
        setObjectId(source.getId());
        setUserId(source.getModifier());
        setIpAddress(ipAddress);
    }

    @Override
    public String getEventType()
    {
        return String.format("%s.%s", EVENT_TYPE, eventAction);
    }

    public String getEventAction()
    {
        return eventAction;
    }

    public void setEventAction(String eventAction)
    {
        this.eventAction = eventAction;
    }
}
