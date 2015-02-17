package com.armedia.acm.services.subscription.model;

/**
 * Created by marjan.stefanoski on 11.02.2015.
 */
public class AcmSubscriptionEventCreatedEvent extends AcmSubscriptionEventPersistenceEvent {

    private static final String EVENT_TYPE = "com.armedia.acm.subscriptionevent.created";

    public AcmSubscriptionEventCreatedEvent( AcmSubscriptionEvent source ) {
        super(source);
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}
