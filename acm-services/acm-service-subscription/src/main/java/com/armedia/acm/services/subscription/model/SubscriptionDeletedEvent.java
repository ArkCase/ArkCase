package com.armedia.acm.services.subscription.model;

import java.util.Date;

/**
 * Created by marjan.stefanoski on 11.02.2015.
 */
public class SubscriptionDeletedEvent extends SubscriptionPersistenceEvent {

    private static final String EVENT_TYPE = "com.armedia.acm.subscription.deleted";

    public SubscriptionDeletedEvent(AcmSubscription source) {
        super(source);
    }

    public SubscriptionDeletedEvent(String userId, Long objectId,String objectType) {
        super(new AcmSubscription());
        setObjectType(objectType);
        setObjectId(objectId);
        setEventDate(new Date());
        setUserId(userId);
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}
