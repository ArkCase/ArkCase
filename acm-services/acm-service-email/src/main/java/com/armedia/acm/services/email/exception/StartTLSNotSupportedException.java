package com.armedia.acm.services.email.exception;

/**
 * @author aleksandar.bujaroski
 */
public class StartTLSNotSupportedException extends Exception
{
    public StartTLSNotSupportedException()
    {
    }

    public StartTLSNotSupportedException(String message)
    {
        super(message);
    }

    public StartTLSNotSupportedException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public StartTLSNotSupportedException(Throwable cause)
    {
        super(cause);
    }

    public StartTLSNotSupportedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
