package com.armedia.acm.plugins.admin.exception;

/**
 * Created by nick.ferguson on 3/22/2017.
 */
public class AcmCmisConfigurationException extends Exception
{
    public AcmCmisConfigurationException()
    {
    }

    public AcmCmisConfigurationException(String message)
    {
        super(message);
    }

    public AcmCmisConfigurationException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public AcmCmisConfigurationException(Throwable cause)
    {
        super(cause);
    }

    public AcmCmisConfigurationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
