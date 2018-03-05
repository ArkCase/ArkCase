package com.armedia.acm.services.subscription.model;

import com.armedia.acm.core.model.AcmEvent;

import java.util.Date;

/**
 * Created by marjan.stefanoski on 11.02.2015.
 */
public class SubscriptionPersistenceEvent extends AcmEvent
{

    public SubscriptionPersistenceEvent(AcmSubscription source)
    {

        super(source);
        setObjectId(source.getSubscriptionId());
        setEventDate(new Date());
        setUserId(source.getUserId());
        setObjectType(SubscriptionConstants.OBJECT_TYPE);
    }

    @Override
    public String getObjectType()
    {
        return SubscriptionConstants.OBJECT_TYPE;
    }

}
