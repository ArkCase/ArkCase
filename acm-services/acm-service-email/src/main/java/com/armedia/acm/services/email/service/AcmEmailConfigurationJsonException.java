package com.armedia.acm.services.email.service;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jun 8, 2017
 *
 */
public class AcmEmailConfigurationJsonException extends AcmEmailConfigurationException
{

    private static final long serialVersionUID = 5418623571837454942L;

    /**
     * @param message
     * @param e
     */
    public AcmEmailConfigurationJsonException(String message, Exception e)
    {
        super(message, e);
    }

}
