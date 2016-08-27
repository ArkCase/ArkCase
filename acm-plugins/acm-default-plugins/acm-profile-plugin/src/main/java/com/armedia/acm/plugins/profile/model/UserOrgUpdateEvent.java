package com.armedia.acm.plugins.profile.model;

public class UserOrgUpdateEvent extends UserOrgPersistentEvent
{

    public UserOrgUpdateEvent(UserOrg source)
    {
        super(source);
    }

    @Override
    public String getEventType()
    {
        return UserOrgConstants.EVENT_TYPE_USER_PROFILE_MODIFIED;
    }
}

