package com.armedia.acm.services.users.model.event;


import com.armedia.acm.core.model.AcmEvent;
import com.armedia.acm.services.users.model.AcmUser;

import java.util.Date;

public abstract class UserPersistenceEvent extends AcmEvent
{

    public UserPersistenceEvent(AcmUser source)
    {
        super(source);
        setUserId(source.getUserId());
        setEventDate(new Date());
    }

}
