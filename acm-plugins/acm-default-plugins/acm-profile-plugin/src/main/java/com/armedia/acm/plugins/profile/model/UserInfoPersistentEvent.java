package com.armedia.acm.plugins.profile.model;

import com.armedia.acm.event.AcmEvent;

import java.util.Date;

/**
 * Created by marjan.stefanoski on 20.10.2014.
 */
public class UserInfoPersistentEvent extends AcmEvent {

    private static final String OBJECT_TYPE = "USER_INFO";

    public UserInfoPersistentEvent(UserInfo source) {
        super(source);
        setEventDate(new Date());
        setUserId(source.getUser().getUserId());
        setObjectId(source.getUserInfoId());
    }
}
