package com.armedia.acm.plugins.objectassociation.model;

import com.armedia.acm.core.model.AcmEvent;

import java.util.Date;


public class UpdateReferenceEvent extends AcmEvent
{

    private static final String EVENT_TYPE = "com.armedia.acm.objectassociation.reference.updated";

    public UpdateReferenceEvent(Reference source)
    {
        super(source);
        setObjectId(source.getParentId());
        setObjectType(source.getParentType());
        setEventDate(new Date());
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}
