package com.armedia.acm.core.exceptions;

public class AcmResourceNotModifiableException extends Exception
{
    public AcmResourceNotModifiableException()
    {
    }

    public AcmResourceNotModifiableException(String message)
    {
        super(message);
    }

    public AcmResourceNotModifiableException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public AcmResourceNotModifiableException(Throwable cause)
    {
        super(cause);
    }

    public AcmResourceNotModifiableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
