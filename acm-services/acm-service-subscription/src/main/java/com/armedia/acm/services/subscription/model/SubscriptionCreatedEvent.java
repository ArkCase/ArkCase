package com.armedia.acm.services.subscription.model;

/**
 * Created by marjan.stefanoski on 11.02.2015.
 */
public class SubscriptionCreatedEvent extends SubscriptionPersistenceEvent {

    private static final String EVENT_TYPE = "com.armedia.acm.subscription.created";

    public SubscriptionCreatedEvent( AcmSubscription source ) {
        super(source);
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}
