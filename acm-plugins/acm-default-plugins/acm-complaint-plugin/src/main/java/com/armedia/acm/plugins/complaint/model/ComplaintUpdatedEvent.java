package com.armedia.acm.plugins.complaint.model;


public class ComplaintUpdatedEvent extends ComplaintPersistenceEvent
{
    private static final String EVENT_TYPE = "com.armedia.acm.complaint.updated";
    private static final long serialVersionUID = 1717774490004890352L;

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
