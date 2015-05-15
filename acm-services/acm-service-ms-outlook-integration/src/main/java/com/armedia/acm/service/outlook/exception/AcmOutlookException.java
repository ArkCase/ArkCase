package com.armedia.acm.service.outlook.exception;

/**
 * Created by armdev on 4/20/15.
 */
public class AcmOutlookException extends RuntimeException
{
    public AcmOutlookException()
    {
    }

    public AcmOutlookException(String message)
    {
        super(message);
    }

    public AcmOutlookException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public AcmOutlookException(Throwable cause)
    {
        super(cause);
    }

    public AcmOutlookException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
