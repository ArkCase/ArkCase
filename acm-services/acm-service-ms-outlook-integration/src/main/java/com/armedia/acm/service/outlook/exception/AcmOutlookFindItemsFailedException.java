package com.armedia.acm.service.outlook.exception;

/**
 * Created by armdev on 4/21/15.
 */
public class AcmOutlookFindItemsFailedException extends AcmOutlookException
{
    public AcmOutlookFindItemsFailedException()
    {
    }

    public AcmOutlookFindItemsFailedException(String message)
    {
        super(message);
    }

    public AcmOutlookFindItemsFailedException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public AcmOutlookFindItemsFailedException(Throwable cause)
    {
        super(cause);
    }

    public AcmOutlookFindItemsFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
