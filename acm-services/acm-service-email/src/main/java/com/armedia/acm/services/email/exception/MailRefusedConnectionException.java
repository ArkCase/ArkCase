package com.armedia.acm.services.email.exception;

/**
 * @author aleksandar.bujaroski
 */
public class MailRefusedConnectionException extends Exception
{
    public MailRefusedConnectionException()
    {
    }

    public MailRefusedConnectionException(String message)
    {
        super(message);
    }

    public MailRefusedConnectionException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public MailRefusedConnectionException(Throwable cause)
    {
        super(cause);
    }

    public MailRefusedConnectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
