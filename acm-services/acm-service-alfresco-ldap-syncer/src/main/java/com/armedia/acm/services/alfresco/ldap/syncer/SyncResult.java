package com.armedia.acm.services.alfresco.ldap.syncer;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jan 2, 2018
 *
 */
public class SyncResult
{
    private boolean success;

    /**
     * @return the success
     */
    public boolean isSuccess()
    {
        return success;
    }

    /**
     * @param success
     *            the success to set
     */
    public void setSuccess(boolean success)
    {
        this.success = success;
    }

}