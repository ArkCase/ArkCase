package com.armedia.acm.plugins.complaint.model;


public class ComplaintUpdatedEvent extends ComplaintPersistenceEvent
{
    private static final String EVENT_TYPE = "com.armedia.acm.complaint.updated";

    public ComplaintUpdatedEvent(Complaint source)
    {
        super(source);
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }

}
