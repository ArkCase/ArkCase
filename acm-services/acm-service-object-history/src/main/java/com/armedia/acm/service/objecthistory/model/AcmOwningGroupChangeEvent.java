package com.armedia.acm.service.objecthistory.model;

import com.armedia.acm.core.model.AcmEvent;

import java.util.Date;

/**
 * Created by teng.wang on 11/16/2016.
 */
public class AcmOwningGroupChangeEvent extends AcmEvent
{
    public static final String EVENT_TYPE = "com.armedia.acm.object.owninggroup.change";

    private static final long serialVersionUID = -3752675258644572742L;

    public AcmOwningGroupChangeEvent(AcmOwningGroup source, String userId)
    {
        super(source);
        setObjectId(source.getId());
        setObjectType("OWNING_GROUP");
        setParentObjectId(source.getObjectId());
        setParentObjectType(source.getObjectType());
        setEventDate(new Date());
        setEventType(EVENT_TYPE);
        setUserId(userId);
        setEventDescription("Owning Group changed from " + source.getOldGroup() + " to " + source.getNewGroup());
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}
