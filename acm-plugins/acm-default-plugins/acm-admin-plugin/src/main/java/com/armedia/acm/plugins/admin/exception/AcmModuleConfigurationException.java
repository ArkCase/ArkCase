package com.armedia.acm.plugins.admin.exception;

/**
 * Created by sergey.kolomiets on 6/26/15.
 */
public class AcmModuleConfigurationException extends Exception{

    public AcmModuleConfigurationException()
    {
    }

    public AcmModuleConfigurationException(String message)
    {
        super(message);
    }

    public AcmModuleConfigurationException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public AcmModuleConfigurationException(Throwable cause)
    {
        super(cause);
    }

    public AcmModuleConfigurationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}



