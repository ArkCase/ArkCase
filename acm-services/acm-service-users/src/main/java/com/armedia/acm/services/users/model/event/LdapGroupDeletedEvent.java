package com.armedia.acm.services.users.model.event;

import com.armedia.acm.services.users.model.group.AcmGroup;

public class LdapGroupDeletedEvent extends GroupPersistenceEvent
{
    //todo Add this static string to ACM conf project
    private static final String EVENT_TYPE = "com.armedia.acm.ldapGroup.deleted";

    public LdapGroupDeletedEvent(AcmGroup source, String groupName)
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
