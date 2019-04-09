package com.armedia.acm.services.notification.event;

import com.armedia.acm.core.model.AcmEvent;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DueDateReminderSentEvent extends AcmEvent
{
    public DueDateReminderSentEvent(Object source, String parentObjectType, Long parentObjectId, Long dueDateRemainingDays)
    {
        super(source);
        setEventDate(new Date());
        setParentObjectType(parentObjectType);
        setParentObjectId(parentObjectId);

        Map<String, Object> eventProperties = new HashMap<>();
        eventProperties.put("dueDateRemainingDays", dueDateRemainingDays);

        setEventProperties(eventProperties);
    }
}
