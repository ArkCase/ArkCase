package com.armedia.acm.plugins.admin.exception;

/**
 * Created by admin on 4/28/15.
 */
public class AcmLdapConfigurationException extends Exception{

    public AcmLdapConfigurationException()
    {
    }

    public AcmLdapConfigurationException(String message)
    {
        super(message);
    }

    public AcmLdapConfigurationException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public AcmLdapConfigurationException(Throwable cause)
    {
        super(cause);
    }

    public AcmLdapConfigurationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}


