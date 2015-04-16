package com.armedia.acm.plugins.profile.model;

import com.armedia.acm.event.AcmEvent;

import java.util.Date;

/**
 * Created by armdev on 4/16/15.
 */
public class OutlookPasswordChangedEvent extends AcmEvent
{
    private static final String EVENT_TYPE = "com.armedia.acm.profile.userorg.outlook_password_changed";

    public OutlookPasswordChangedEvent(UserOrg userOrg, String userId, String ipAddress, boolean succeeded)
    {
        super(userId);
        setIpAddress(ipAddress);
        setUserId(userId);
        setEventDate(new Date());
        setObjectId(userOrg == null ? null : userOrg.getUserOrgId());
        setObjectType(UserOrgConstants.OBJECT_TYPE);
        setEventType(EVENT_TYPE);
        setSucceeded(succeeded);
    }
}
