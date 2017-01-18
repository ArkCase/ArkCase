package com.armedia.acm.services.email.event;

import com.armedia.acm.core.model.AcmEvent;

import java.util.Date;

/**
 * Created by ivo.shurbanovski on 1/17/2017.
 */
public class SmtpEmailReceivedEvent extends AcmEvent {
    private static final String EVENT_TYPE = "com.armedia.acm.smtp.event.received";

    public SmtpEmailReceivedEvent(Object source, String userId, Long objectId, String objectType)
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
    public String getEventType() {
        return EVENT_TYPE;
    }
}
