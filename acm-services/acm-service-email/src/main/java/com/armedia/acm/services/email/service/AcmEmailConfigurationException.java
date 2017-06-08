package com.armedia.acm.services.email.service;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 28, 2017
 *
 */
public class AcmEmailConfigurationException extends AcmEmailServiceException
{

    private static final long serialVersionUID = 1L;

    /**
     * @param format
     */
    public AcmEmailConfigurationException(String message)
    {
        super(message);
    }

    /**
     * @param message
     * @param e
     */
    public AcmEmailConfigurationException(String message, Exception e)
    {
        super(message, e);
    }

}
