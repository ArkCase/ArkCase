package com.armedia.acm.services.costsheet.model;

import java.util.Date;

import com.armedia.acm.core.model.AcmEvent;
import com.armedia.acm.data.AcmEntity;

public class AcmCostsheetAssociatedEvent extends AcmEvent
{

    private static final long serialVersionUID = -3652527955108616909L;    
    
    public AcmCostsheetAssociatedEvent(AcmEntity source, Long sourceId, String sourceType, String eventType, String userId, String ipAddress, Date eventDate, boolean succeeded)
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
