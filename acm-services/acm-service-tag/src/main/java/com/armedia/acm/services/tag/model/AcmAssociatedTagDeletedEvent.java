package com.armedia.acm.services.tag.model;


public class AcmAssociatedTagDeletedEvent extends AcmAssociatedTagPersistentEvent{
    private static final String EVENT_TYPE = "com.armedia.acm.associatedtag.deleted";

    public AcmAssociatedTagDeletedEvent( AcmAssociatedTag source,String userId ) {
        super(source,userId);
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}
