package com.armedia.acm.services.subscription.model;

import com.armedia.acm.core.model.AcmEvent;

import java.util.Date;

/**
 * Created by marjan.stefanoski on 11.02.2015.
 */
public class AcmSubscriptionEventPersistenceEvent extends AcmEvent
{

    public AcmSubscriptionEventPersistenceEvent(AcmSubscriptionEvent source)
    {

        super(source);
        setObjectId(source.getSubscriptionEventId());
        setEventDate(new Date());
        setUserId(source.getEventUser());
        setObjectType(SubscriptionConstants.OBJECT_TYPE_EVENT);
    }

    @Override
    public String getObjectType()
    {
        return SubscriptionConstants.OBJECT_TYPE_EVENT;
    }

}
