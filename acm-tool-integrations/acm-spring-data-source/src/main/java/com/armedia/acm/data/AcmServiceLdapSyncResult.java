package com.armedia.acm.data;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jan 4, 2018
 *
 */
public class AcmServiceLdapSyncResult
{

    private String service;

    private String user;

    private boolean result;

    private String message;

    /**
     * @return the service
     */
    public String getService()
    {
        return service;
    }

    /**
     * @param service
     *            the service to set
     */
    public void setService(String service)
    {
        this.service = service;
    }

    /**
     * @return the user
     */
    public String getUser()
    {
        return user;
    }

    /**
     * @param user
     *            the user to set
     */
    public void setUser(String user)
    {
        this.user = user;
    }

    /**
     * @return the result
     */
    public boolean isResult()
    {
        return result;
    }

    /**
     * @param result
     *            the result to set
     */
    public void setResult(boolean result)
    {
        this.result = result;
    }

    /**
     * @return the message
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * @param message
     *            the message to set
     */
    public void setMessage(String message)
    {
        this.message = message;
    }

}
