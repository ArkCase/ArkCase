package com.armedia.acm.services.tag.model;

public class AcmAssociatedTagCreatedEvent extends AcmAssociatedTagPersistentEvent{
    private static final String EVENT_TYPE = "com.armedia.acm.associatedtag.created";

    public AcmAssociatedTagCreatedEvent( AcmAssociatedTag source,String userId ) {
        super(source,userId);
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}
