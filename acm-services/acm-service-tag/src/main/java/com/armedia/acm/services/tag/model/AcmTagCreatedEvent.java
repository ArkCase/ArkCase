package com.armedia.acm.services.tag.model;


public class AcmTagCreatedEvent extends  AcmTagPersistenceEvent{

    private static final String EVENT_TYPE = "com.armedia.acm.tag.created";

    public AcmTagCreatedEvent( AcmTag source,String userId ) {
        super(source,userId);
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}
