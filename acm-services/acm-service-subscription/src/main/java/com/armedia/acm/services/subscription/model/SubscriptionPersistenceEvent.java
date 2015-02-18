package com.armedia.acm.services.subscription.model;

import com.armedia.acm.event.AcmEvent;

import java.util.Date;

/**
 * Created by marjan.stefanoski on 11.02.2015.
 */
public class SubscriptionPersistenceEvent extends AcmEvent {

    private static final String OBJECT_TYPE = "SUBSCRIPTION";

    public SubscriptionPersistenceEvent(AcmSubscription source) {

        super(source);
        setObjectId(source.getSubscriptionId());
        setEventDate(new Date());
        setUserId(source.getUserId());
        setObjectType(OBJECT_TYPE);
    }

    @Override
    public String getObjectType()
    {
        return OBJECT_TYPE;
    }

}
