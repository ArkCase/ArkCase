package com.armedia.acm.plugins.profile.model;

/**
 * Created by marjan.stefanoski on 20.10.2014.
 */
public class UserOrgUpdateEvent extends UserOrgPersistentEvent {

    public UserOrgUpdateEvent(UserOrg source) {
        super(source);
    }

    @Override
    public String getEventType() {
        return UserOrgConstants.EVENT_TYPE_USER_PROFILE_MODIFIED;
    }
}

