package com.armedia.acm.plugins.person.model;

import com.armedia.acm.core.model.AcmEvent;

import java.util.Date;

public class OrganizationEvent extends AcmEvent
{
    private static final long serialVersionUID = 1L;

    private static final String EVENT_TYPE = "com.armedia.acm.organization";

    private String eventStatus;

    public OrganizationEvent(Organization source)
    {

        super(source);

        setObjectId(source.getOrganizationId());
        setObjectType(source.getObjectType());
        setEventDate(new Date());
        setUserId(source.getModifier());
    }

    public OrganizationEvent(Organization source, String eventStatus)
    {

        super(source);

        setObjectId(source.getOrganizationId());
        setObjectType(source.getObjectType());
        setEventDate(new Date());
        setUserId(source.getModifier());
        this.eventStatus = eventStatus;
    }

    @Override
    public String getEventType()
    {
        return String.format("%s.%s", EVENT_TYPE, eventStatus);
    }

    public String getEventStatus()
    {
        return eventStatus;
    }

    public void setEventStatus(String eventStatus)
    {
        this.eventStatus = eventStatus;
    }
}
