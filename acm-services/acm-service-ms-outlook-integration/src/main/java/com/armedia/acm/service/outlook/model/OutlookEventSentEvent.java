package com.armedia.acm.service.outlook.model;


import com.armedia.acm.core.model.AcmEvent;

import java.util.Date;

public class OutlookEventSentEvent extends AcmEvent
{
    private static final long serialVersionUID = 1L;
    private static final String EVENT_TYPE = "com.armedia.acm.outlook.email.event.sent";

    public OutlookEventSentEvent(EmailWithAttachmentsDTO source, String userId, Long objectId, String eventType)
    {
        super(source);
        setObjectId(objectId);
        setUserId(userId);
        setEventDate(new Date());
        setEventType(eventType);
    }

    public OutlookEventSentEvent(EmailWithEmbeddedLinksDTO source, String userId, Long objectId, String eventType)
    {
        super(source);
        setObjectId(objectId);
        setUserId(userId);
        setEventDate(new Date());
        setEventType(eventType);
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}