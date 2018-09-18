package com.armedia.acm.services.email.smtp;

import com.armedia.acm.core.model.AcmEvent;

import java.util.Date;

/**
 * @author sasko.tanaskoski
 *
 */
public class SmtpSentEventHyperlink extends AcmEvent
{
    private static final long serialVersionUID = 1814130260773517605L;
    private static final String EVENT_TYPE = "com.armedia.acm.smtp.event.sent.hyperlink";

    public SmtpSentEventHyperlink(Object source, String userId)
    {
        this(source, userId, null, null);
    }

    public SmtpSentEventHyperlink(Object source, String userId, Long objectId, String objectType)
    {
        super(source);
        setUserId(userId);
        setEventDate(new Date());
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
