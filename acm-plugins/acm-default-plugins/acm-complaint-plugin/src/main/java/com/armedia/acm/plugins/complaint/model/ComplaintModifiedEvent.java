package com.armedia.acm.plugins.complaint.model;

public class ComplaintModifiedEvent extends ComplaintPersistenceEvent
{

    private static final long serialVersionUID = 2601901328541042900L;
    private static final String EVENT_TYPE = "com.armedia.acm.complaint";

    private String eventStatus;

    public ComplaintModifiedEvent(Complaint source)
    {
        super(source);
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