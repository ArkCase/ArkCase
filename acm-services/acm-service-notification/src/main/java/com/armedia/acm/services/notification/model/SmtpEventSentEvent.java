package com.armedia.acm.services.notification.model;


import com.armedia.acm.core.model.AcmEvent;

import java.util.Date;

public class SmtpEventSentEvent extends AcmEvent
{
    private static final long serialVersionUID = 1L;
    private static final String EVENT_TYPE = "com.armedia.acm.outlook.smtp.event.sent";

    public SmtpEventSentEvent(Object source, String userId)
    {
        super(source);
        setUserId(userId);
        setEventDate(new Date());
        setEventType("SMTP SEND");
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}