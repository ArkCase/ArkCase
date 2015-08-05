package com.armedia.acm.plugins.profile.model;

import com.armedia.acm.core.model.AcmEvent;

import java.util.Date;

/**
 * Created by marjan.stefanoski on 20.10.2014.
 */
public class GetProfileInfoEvent extends AcmEvent
{

    private static final String EVENT_TYPE = "com.armedia.acm.profile.getByUserId";

    private static final String OBJECT_TYPE = "PROFILE";

    public GetProfileInfoEvent(ProfileDTO source) {
        super(source);
        setUserId(source.getUserId());
        setEventDate(new Date());
        setObjectType(OBJECT_TYPE);
        setEventType(EVENT_TYPE);
    }

}
