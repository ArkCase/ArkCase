package com.armedia.acm.services.search.model;

import java.util.Date;

import com.armedia.acm.event.AcmEvent;

public class ApplicationSearchEvent extends AcmEvent {
    /**
     * 
     */
    private static final long serialVersionUID = 4431386574969239951L;

    private static final String EVENT_TYPE = "com.armedia.acm.plugins.search.result";
    
    public ApplicationSearchEvent(Long objectId, String objectType,
            String eventUser,            
            boolean succeeded, String ipAddress)
    {
        super(objectId);
        
        setObjectId(objectId);
        setObjectType(objectType);
        setEventDate(new Date());
        setEventType(getEventType());
        setUserId(eventUser);
        setSucceeded(succeeded);
        setIpAddress(ipAddress);
    }
    
    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}
