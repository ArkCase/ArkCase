package com.armedia.acm.plugins.complaint.model;

import com.armedia.acm.event.AcmEvent;

import java.util.Date;

/**
 * One event raised per search result.
 */
public class ComplaintSearchResultEvent extends AcmEvent
{
    private static final String EVENT_TYPE = "com.armedia.acm.complaint.search.result";
    private static final long serialVersionUID = -7440634052215762578L;

    public ComplaintSearchResultEvent(ComplaintListView source)
    {
        super(source);
        setObjectId(source.getComplaintId());
        setEventDate(new Date());
        setObjectType("COMPLAINT");
        setSucceeded(true);
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}
