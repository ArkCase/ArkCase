package com.armedia.acm.plugins.complaint.model;


public class ComplaintCreatedEvent extends ComplaintPersistenceEvent
{
    private static final String EVENT_TYPE = "com.armedia.acm.complaint.created";
    private static final long serialVersionUID = -2106369196707219490L;

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
