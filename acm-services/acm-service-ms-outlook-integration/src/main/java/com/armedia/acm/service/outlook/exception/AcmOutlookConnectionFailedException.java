package com.armedia.acm.service.outlook.exception;

/**
 * Created by armdev on 4/20/15.
 */
public class AcmOutlookConnectionFailedException extends AcmOutlookException
{
    public AcmOutlookConnectionFailedException()
    {
    }

    public AcmOutlookConnectionFailedException(String message)
    {
        super(message);
    }

    public AcmOutlookConnectionFailedException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public AcmOutlookConnectionFailedException(Throwable cause)
    {
        super(cause);
    }

    public AcmOutlookConnectionFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
