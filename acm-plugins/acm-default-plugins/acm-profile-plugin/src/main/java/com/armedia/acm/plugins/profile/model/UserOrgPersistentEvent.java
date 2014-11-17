package com.armedia.acm.plugins.profile.model;

import com.armedia.acm.event.AcmEvent;

import java.util.Date;

/**
 * Created by marjan.stefanoski on 20.10.2014.
 */
public class UserOrgPersistentEvent extends AcmEvent {

    private static final String OBJECT_TYPE = "USER_ORG";

    public UserOrgPersistentEvent(UserOrg source) {
        super(source);
        setEventDate(new Date());
        setUserId(source.getUser().getUserId());
        setObjectId(source.getUserOrgId());
    }

    @Override
    public String getObjectType()
    {
        return OBJECT_TYPE;
    }

}
