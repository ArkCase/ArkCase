package com.armedia.acm.plugins.profile.model;

import com.armedia.acm.core.model.AcmEvent;

import java.util.Date;

public class UserOrgPersistentEvent extends AcmEvent
{

    public UserOrgPersistentEvent(UserOrg source)
    {
        super(source);
        setEventDate(new Date());
        setUserId(source.getUser().getUserId());
        setObjectId(source.getUserOrgId());
    }

    @Override
    public String getObjectType()
    {
        return UserOrgConstants.OBJECT_TYPE;
    }

}
