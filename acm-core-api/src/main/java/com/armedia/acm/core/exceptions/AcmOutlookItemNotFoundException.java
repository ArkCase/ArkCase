package com.armedia.acm.core.exceptions;

/**
 * Created by nebojsha on 04.05.2015.
 */
public class AcmOutlookItemNotFoundException extends AcmOutlookException
{
    public AcmOutlookItemNotFoundException()
    {
    }

    public AcmOutlookItemNotFoundException(String message)
    {
        super(message);
    }

    public AcmOutlookItemNotFoundException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public AcmOutlookItemNotFoundException(Throwable cause)
    {
        super(cause);
    }

    public AcmOutlookItemNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
