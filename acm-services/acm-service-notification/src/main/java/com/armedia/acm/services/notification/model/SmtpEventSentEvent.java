package com.armedia.acm.services.notification.model;


import com.armedia.acm.core.model.AcmEvent;

import java.util.Date;

public class SmtpEventSentEvent extends AcmEvent
{
    private static final long serialVersionUID = 1L;
    private static final String EVENT_TYPE = "com.armedia.acm.smtp.event.sent";

    public SmtpEventSentEvent(Object source, String userId)
    {
        this(source, userId, null, null);
    }

    public SmtpEventSentEvent(Object source, String userId, Long objectId, String objectType)
    {
        super(source);
        setUserId(userId);
        setEventDate(new Date());;
        if (objectId != null && objectType != null)
        {
            setObjectId(objectId);
            setObjectType(objectType);
        }
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}