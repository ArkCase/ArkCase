package com.armedia.acm.plugins.complaint.model;


public class ComplaintCreatedEvent extends ComplaintPersistenceEvent
{
    private static final String EVENT_TYPE = "com.armedia.acm.complaint.created";

    public ComplaintCreatedEvent(Complaint source)
    {
        super(source);
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }

}
