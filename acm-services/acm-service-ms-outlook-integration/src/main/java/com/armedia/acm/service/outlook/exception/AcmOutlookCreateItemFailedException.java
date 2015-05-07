package com.armedia.acm.service.outlook.exception;

/**
 * Created by armdev on 4/20/15.
 */
public class AcmOutlookCreateItemFailedException extends AcmOutlookException
{
    public AcmOutlookCreateItemFailedException()
    {
    }

    public AcmOutlookCreateItemFailedException(String message)
    {
        super(message);
    }

    public AcmOutlookCreateItemFailedException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public AcmOutlookCreateItemFailedException(Throwable cause)
    {
        super(cause);
    }

    public AcmOutlookCreateItemFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
