package com.armedia.acm.plugins.complaint.model;

import com.armedia.acm.event.AcmEvent;

import java.util.Date;

/**
 * Created by armdev on 6/4/14.
 */
public class FindComplaintByIdEvent extends AcmEvent
{
    private static final long serialVersionUID = -2912148000260551565L;

    public FindComplaintByIdEvent(Complaint source)
    {
        super(source);

        setEventType("com.armedia.acm.complaint.findById");
        setObjectId(source.getComplaintId());
        setEventDate(new Date());
        setObjectType("COMPLAINT");
    }
}
