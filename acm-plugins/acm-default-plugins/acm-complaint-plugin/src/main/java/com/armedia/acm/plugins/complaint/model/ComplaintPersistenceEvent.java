package com.armedia.acm.plugins.complaint.model;

import com.armedia.acm.event.AcmEvent;

import java.util.Date;

/**
 * Created by armdev on 4/11/14.
 */
public abstract class ComplaintPersistenceEvent extends AcmEvent
{
    private static final String OBJECT_TYPE = "COMPLAINT";
    private String complaintNumber;

    public ComplaintPersistenceEvent(Complaint source)
    {
        super(source);
        setObjectId(source.getComplaintId());
        setEventDate(new Date());
        setUserId(source.getModifier());
        setComplaintNumber(source.getComplaintNumber());
    }

    @Override
    public String getObjectType()
    {
        return OBJECT_TYPE;
    }

    public String getComplaintNumber()
    {
        return complaintNumber;
    }

    public void setComplaintNumber(String complaintNumber)
    {
        this.complaintNumber = complaintNumber;
    }
}
