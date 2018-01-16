package com.armedia.acm.service.outlook.model;

import com.armedia.acm.core.model.AcmEvent;

import java.util.Date;

public class CalendarEventAddedEvent extends AcmEvent
{
    private static final long serialVersionUID = 1L;
    private static final String EVENT_TYPE = "com.armedia.acm.outlook.calendar.event.added";

    public CalendarEventAddedEvent(OutlookCalendarItem source, String userId, Long objectId, String objectType)
    {
        super(source);
        setEventDate(new Date());
        setObjectId(objectId);
        setObjectType(objectType);
        setUserId(userId);
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}
