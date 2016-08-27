package com.armedia.acm.plugins.profile.model;

public class UserOrgCreatedEvent extends UserOrgPersistentEvent
{

    public UserOrgCreatedEvent(UserOrg source)
    {
        super(source);
    }

    @Override
    public String getEventType()
    {
        return UserOrgConstants.EVENT_TYPE_USER_PROFILE_CREATED;
    }
}
