package com.armedia.acm.services.tag.model;


public class AcmTagUpdatedEvent extends AcmTagPersistenceEvent {
    private static final String EVENT_TYPE = "com.armedia.acm.tag.updated";

    public AcmTagUpdatedEvent( AcmTag source,String userId ) {
        super(source,userId);
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }

}
