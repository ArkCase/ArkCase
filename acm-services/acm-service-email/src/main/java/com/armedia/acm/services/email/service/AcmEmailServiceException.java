package com.armedia.acm.services.email.service;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 28, 2017
 *
 */
public class AcmEmailServiceException extends Exception
{

    private static final long serialVersionUID = 1L;

    /**
     * @param message
     */
    public AcmEmailServiceException(String message)
    {
        super(message);
    }

    /**
     * @param string
     * @param e
     */
    public AcmEmailServiceException(String message, Exception e)
    {
        super(message, e);
    }

}
