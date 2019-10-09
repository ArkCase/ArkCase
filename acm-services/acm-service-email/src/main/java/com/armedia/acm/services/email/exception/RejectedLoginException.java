package com.armedia.acm.services.email.exception;

/**
 * @author aleksandar.bujaroski
 */
public class RejectedLoginException extends Exception
{
    public RejectedLoginException()
    {
    }

    public RejectedLoginException(String message)
    {
        super(message);
    }

    public RejectedLoginException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public RejectedLoginException(Throwable cause)
    {
        super(cause);
    }

    public RejectedLoginException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
