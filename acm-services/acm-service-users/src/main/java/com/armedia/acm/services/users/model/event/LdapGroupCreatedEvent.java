package com.armedia.acm.services.users.model.event;

import com.armedia.acm.services.users.model.group.AcmGroup;

public class LdapGroupCreatedEvent extends GroupPersistenceEvent
{
    private static final String EVENT_TYPE = "com.armedia.acm.ldapGroup.created";

    public LdapGroupCreatedEvent(AcmGroup source, String groupName)
    {
        super(source);
        setUserId(groupName);
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}
