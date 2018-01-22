package com.armedia.acm.plugins.admin.exception;

/**
 * Created by sergey on 4/13/16.
 */
public class AcmPropertiesManagementException extends Exception
{

    public AcmPropertiesManagementException()
    {
    }

    public AcmPropertiesManagementException(String message)
    {
        super(message);
    }

    public AcmPropertiesManagementException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public AcmPropertiesManagementException(Throwable cause)
    {
        super(cause);
    }

    public AcmPropertiesManagementException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
