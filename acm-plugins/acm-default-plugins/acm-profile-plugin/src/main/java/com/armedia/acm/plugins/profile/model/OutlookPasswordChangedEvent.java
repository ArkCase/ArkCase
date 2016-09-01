package com.armedia.acm.plugins.profile.model;

import com.armedia.acm.core.model.AcmEvent;
import com.armedia.acm.service.outlook.model.OutlookConstants;
import com.armedia.acm.service.outlook.model.OutlookPassword;

import java.util.Date;

public class OutlookPasswordChangedEvent extends AcmEvent
{
    private static final long serialVersionUID = -1864933375071122405L;

    public OutlookPasswordChangedEvent(OutlookPassword outlookPassword, String userId, String ipAddress, boolean succeeded)
    {
        super(userId);
        setIpAddress(ipAddress);
        setUserId(userId);
        setEventDate(new Date());
        // setObjectId(userOrg == null ? null : userOrg.getUserOrgId());
        // setObjectType(UserOrgConstants.OBJECT_TYPE);
        setEventType(OutlookConstants.EVENT_TYPE_OUTLOOK_PASSWORD_CHANGED);
        setSucceeded(succeeded);
    }
}
