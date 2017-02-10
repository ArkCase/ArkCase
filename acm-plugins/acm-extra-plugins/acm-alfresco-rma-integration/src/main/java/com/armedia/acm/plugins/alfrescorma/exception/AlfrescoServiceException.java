package com.armedia.acm.plugins.alfrescorma.exception;

/**
 * Created by dmiller on 11/7/2016.
 */
public class AlfrescoServiceException extends Exception
{
    public AlfrescoServiceException()
    {
    }

    public AlfrescoServiceException(String message)
    {
        super(message);
    }

    public AlfrescoServiceException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public AlfrescoServiceException(Throwable cause)
    {
        super(cause);
    }

    public AlfrescoServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
