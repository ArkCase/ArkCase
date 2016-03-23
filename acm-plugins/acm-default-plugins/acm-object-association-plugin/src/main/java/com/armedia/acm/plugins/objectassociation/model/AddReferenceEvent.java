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
    private static final String EVENT_TYPE = "com.armedia.acm.objectassociaton.reference.added";

    public AddReferenceEvent(Reference source)
    {

        super(source);
        setObjectId(source.getTargetId());
        setEventDate(new Date());
        setObjectType(source.getTargetType());
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}
