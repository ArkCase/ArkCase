package com.armedia.acm.services.ldap.syncer;

import org.springframework.context.ApplicationEvent;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Feb 1, 2018
 *
 */
public class AcmLdapSyncEvent extends ApplicationEvent
{

    private static final long serialVersionUID = -2643447407954711297L;

    /**
     * @param source
     */
    public AcmLdapSyncEvent(Object source)
    {
        super(source);
    }

}
