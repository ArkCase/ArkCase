package com.armedia.acm.services.email.service;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jun 8, 2017
 *
 */
public class AcmEmailConfigurationIOException extends AcmEmailConfigurationException
{

    private static final long serialVersionUID = 4226801360811402125L;

    /**
     * @param message
     */
    public AcmEmailConfigurationIOException(String message)
    {
        super(message);
    }

    /**
     * @param message
     * @param e
     */
    public AcmEmailConfigurationIOException(String message, Exception e)
    {
        super(message, e);
    }

}
