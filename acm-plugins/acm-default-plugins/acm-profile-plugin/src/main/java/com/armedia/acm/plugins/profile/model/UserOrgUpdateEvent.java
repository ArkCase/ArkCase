package com.armedia.acm.plugins.profile.model;

/**
 * Created by marjan.stefanoski on 20.10.2014.
 */
public class UserOrgUpdateEvent extends UserOrgPersistentEvent {

    private static final String EVENT_TYPE = "com.armedia.acm.profile.userorg.updated";

    public UserOrgUpdateEvent(UserOrg source) {
        super(source);
    }

    @Override
    public String getEventType() {
        return EVENT_TYPE;
    }
}

