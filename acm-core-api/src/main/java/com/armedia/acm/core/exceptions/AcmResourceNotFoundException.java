package com.armedia.acm.core.exceptions;

public class AcmResourceNotFoundException extends Exception
{
    public AcmResourceNotFoundException()
    {
    }

    public AcmResourceNotFoundException(String message)
    {
        super(message);
    }

    public AcmResourceNotFoundException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public AcmResourceNotFoundException(Throwable cause)
    {
        super(cause);
    }

    public AcmResourceNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
