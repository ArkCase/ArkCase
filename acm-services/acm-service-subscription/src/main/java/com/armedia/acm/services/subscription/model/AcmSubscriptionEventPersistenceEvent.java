package com.armedia.acm.services.subscription.model;

import com.armedia.acm.event.AcmEvent;

import java.util.Date;

/**
 * Created by marjan.stefanoski on 11.02.2015.
 */
public class AcmSubscriptionEventPersistenceEvent extends AcmEvent {

    private static final String OBJECT_TYPE = "SUBSCRIPTION_EVENT";

    public AcmSubscriptionEventPersistenceEvent(AcmSubscriptionEvent source) {

        super(source);
        setObjectId(source.getSubscriptionEventId());
        setEventDate(new Date());
        setUserId(source.getEventUser());
        setObjectType(OBJECT_TYPE);
    }

    @Override
    public String getObjectType()
    {
        return OBJECT_TYPE;
    }

}

