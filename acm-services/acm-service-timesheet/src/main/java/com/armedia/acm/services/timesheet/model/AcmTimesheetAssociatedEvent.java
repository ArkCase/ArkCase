package com.armedia.acm.services.timesheet.model;

import java.util.Date;

import com.armedia.acm.core.AcmStatefulEntity;
import com.armedia.acm.core.model.AcmEvent;

public class AcmTimesheetAssociatedEvent extends AcmEvent
{

    private static final long serialVersionUID = 4922835593497915522L;
    
    public AcmTimesheetAssociatedEvent(AcmStatefulEntity source, Long sourceId, String sourceType, String eventType, String userId, String ipAddress, Date eventDate, boolean succeeded)
    {
        super(source);
        
        setObjectId(sourceId);
        setObjectType(sourceType);
        setEventType(eventType);
        setUserId(userId);
        setIpAddress(ipAddress);
        setEventDate(eventDate);        
        setSucceeded(succeeded);
    }
}
