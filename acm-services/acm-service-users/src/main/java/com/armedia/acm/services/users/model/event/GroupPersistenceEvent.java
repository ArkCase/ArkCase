package com.armedia.acm.services.users.model.event;


import com.armedia.acm.core.model.AcmEvent;
import com.armedia.acm.services.users.model.group.AcmGroup;

import java.util.Date;

public abstract class GroupPersistenceEvent extends AcmEvent
{

    public GroupPersistenceEvent(AcmGroup source)
    {
        super(source);
        setUserId(source.getName());
        setEventDate(new Date());
    }

}
