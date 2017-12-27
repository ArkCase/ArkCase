package com.armedia.acm.plugins.objectassociation.model;

import com.armedia.acm.core.model.AcmEvent;

import java.util.Date;

/**
 * 
 * @author vladimir.radeski
 *
 */

public class AddReferenceEvent extends AcmEvent
{

    private static final long serialVersionUID = 6217892527760951563L;
    private static final String EVENT_TYPE = "com.armedia.acm.objectassociation.reference.added";

    public AddReferenceEvent(ObjectAssociation source)
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
