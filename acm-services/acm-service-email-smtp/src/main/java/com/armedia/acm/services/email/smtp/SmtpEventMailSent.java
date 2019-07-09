package com.armedia.acm.services.email.smtp;

import com.armedia.acm.core.model.AcmEvent;

import java.util.Date;

public class SmtpEventMailSent extends AcmEvent {

    private static final long serialVersionUID = 1L;
    private static final String EVENT_TYPE = "com.armedia.acm.smtp.event.mail.sent";

    public SmtpEventMailSent(Object source, String userId)
    {
        this(source, userId, null, null, null);
    }

    public SmtpEventMailSent(Object source, String userId, Long objectId, String objectType, String ipAddress)
    {
        super(source);
        setUserId(userId);
        setEventDate(new Date());
        if (objectId != null && objectType != null)
        {
            setObjectId(objectId);
            setObjectType(objectType);
            setIpAddress(ipAddress);
        }
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}
