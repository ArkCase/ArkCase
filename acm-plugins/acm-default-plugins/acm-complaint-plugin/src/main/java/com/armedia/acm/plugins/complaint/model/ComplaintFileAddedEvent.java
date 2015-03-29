package com.armedia.acm.plugins.complaint.model;

import java.util.Date;

/**
 * Created by marjan.stefanoski on 11.02.2015.
 */
public class ComplaintFileAddedEvent extends ComplaintPersistenceEvent {

    private static final String EVENT_TYPE = "com.armedia.acm.complaint.file.added";
    private static final long serialVersionUID = -2343847954432215770L;

    public ComplaintFileAddedEvent( Complaint source ) {

        super(source);
        setObjectId(source.getComplaintId());
        setEventDate(new Date());
        setObjectType(source.getObjectType());
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}
