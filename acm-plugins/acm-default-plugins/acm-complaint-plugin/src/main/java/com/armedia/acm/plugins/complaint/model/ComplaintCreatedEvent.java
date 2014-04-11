package com.armedia.acm.plugins.complaint.model;


import com.armedia.acm.event.AcmEvent;

import java.util.Date;

public class ComplaintCreatedEvent extends AcmEvent
{
    private static final String EVENT_TYPE = "com.armedia.acm.complaint.created";
    private static final String OBJECT_TYPE = "COMPLAINT";

    public ComplaintCreatedEvent(Complaint source)
    {
        super(source);
        setObjectId(source.getComplaintId());
        setEventDate(new Date());
        setUserId(source.getModifier());
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }

    @Override
    public String getObjectType()
    {
        return OBJECT_TYPE;
    }
}
