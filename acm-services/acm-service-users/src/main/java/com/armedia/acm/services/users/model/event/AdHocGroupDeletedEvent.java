package com.armedia.acm.services.users.model.event;

import com.armedia.acm.services.users.model.group.AcmGroup;

public class AdHocGroupDeletedEvent extends GroupPersistenceEvent
{

    private static final String EVENT_TYPE = "com.armedia.acm.adHocGroup.deleted";

    public AdHocGroupDeletedEvent(AcmGroup source)
    {
        super(source);
        setUserId(source.getModifier());
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}
