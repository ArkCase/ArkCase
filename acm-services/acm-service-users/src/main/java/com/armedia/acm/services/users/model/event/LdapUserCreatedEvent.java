package com.armedia.acm.services.users.model.event;

import com.armedia.acm.services.users.model.AcmUser;
import org.springframework.security.core.Authentication;

public class LdapUserCreatedEvent extends UserPersistenceEvent
{
    private static final long serialVersionUID = -17652493557L;
    private static final String EVENT_TYPE = "com.armedia.acm.ldapUser.created";

    public LdapUserCreatedEvent(AcmUser user, boolean succeeded, String ipAddress, Authentication auth)
    {
        super(user);

        setObjectType("USER");
        setEventDate(user.getModified());
        setUserId(auth.getName());
        setEventType(EVENT_TYPE);
        setSucceeded(succeeded);
        setIpAddress(ipAddress);
    }
}