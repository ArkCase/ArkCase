package com.armedia.acm.data;

import org.springframework.context.ApplicationEvent;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jan 4, 2018
 *
 */
public class AcmServiceLdapSyncEvent extends ApplicationEvent
{

    private static final long serialVersionUID = -858292750547038300L;

    private AcmServiceLdapSyncResult syncResult;

    /**
     * @param syncResult
     */
    public AcmServiceLdapSyncEvent(AcmServiceLdapSyncResult source)
    {
        super(source);
        syncResult = source;
    }

    /**
     * @return the syncResult
     */
    public AcmServiceLdapSyncResult getSyncResult()
    {
        return syncResult;
    }

}
