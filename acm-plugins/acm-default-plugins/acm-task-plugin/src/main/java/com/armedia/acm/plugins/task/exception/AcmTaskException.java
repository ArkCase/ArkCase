package com.armedia.acm.plugins.task.exception;

/**
 * Created by armdev on 6/3/14.
 */
public class AcmTaskException extends Exception
{

    public AcmTaskException()
    {
    }

    public AcmTaskException(String message)
    {
        super(message);
    }

    public AcmTaskException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public AcmTaskException(Throwable cause)
    {
        super(cause);
    }

    public AcmTaskException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
