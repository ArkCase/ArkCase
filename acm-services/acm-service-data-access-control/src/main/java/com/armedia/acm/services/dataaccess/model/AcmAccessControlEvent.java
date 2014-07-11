package com.armedia.acm.services.dataaccess.model;

import com.armedia.acm.event.AcmEvent;

import java.util.Date;

/**
 * Created by armdev on 7/11/14.
 */
public class AcmAccessControlEvent extends AcmEvent
{
    public AcmAccessControlEvent(AcmAccessControlDefault source, String eventType, String user, boolean succeeded,
                                 String ipAddress)
    {
        super(source);

        setObjectType("ACCESS_CONTROL_DEFAULT");
        setEventType("com.armedia.acm.accessControlDefault." + eventType);
        setSucceeded(succeeded);
        setIpAddress(ipAddress);
        setEventDate(new Date());
        setObjectId(source.getId());
        setUserId(user);
    }
}
