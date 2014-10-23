package com.armedia.acm.plugins.profile.model;

import com.armedia.acm.event.AcmEvent;

/**
 * Created by marjan.stefanoski on 20.10.2014.
 */
public class UserInfoUpdateEvent extends UserInfoPersistentEvent {

    private static final String EVENT_TYPE = "com.armedia.acm.profile.userInfo.updated";

    public UserInfoUpdateEvent(UserInfo source) {
        super(source);
    }

    @Override
    public String getEventType() {
        return EVENT_TYPE;
    }
}

