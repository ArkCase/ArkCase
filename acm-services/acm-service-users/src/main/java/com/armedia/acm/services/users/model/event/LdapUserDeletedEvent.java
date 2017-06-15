package com.armedia.acm.services.users.model.event;

import com.armedia.acm.services.users.model.AcmUser;

public class LdapUserDeletedEvent extends UserPersistenceEvent
{

    private static final String EVENT_TYPE = "com.armedia.acm.ldapUser.deleted";

    public LdapUserDeletedEvent(AcmUser source, String userId)
    {
        super(source);

        setUserId(userId);
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}
