package com.armedia.acm.plugins.objectassociation.model;

import com.armedia.acm.core.model.AcmEvent;

import java.util.Date;


public class DeleteReferenceEvent extends AcmEvent
{

    private static final String EVENT_TYPE = "com.armedia.acm.objectassociation.reference.deleted";

    public DeleteReferenceEvent(ObjectAssociation source)
    {

        super(source);
        setEventDate(new Date());
        setObjectId(source.getTargetId());
        setObjectType(source.getTargetType());
        setParentObjectType(source.getParentType());
        setParentObjectId(source.getParentId());
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}
