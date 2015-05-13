package com.armedia.acm.service.outlook.exception;

public class AcmOutlookModifyItemFailedException extends AcmOutlookException
{
    public AcmOutlookModifyItemFailedException()
    {
    }

    public AcmOutlookModifyItemFailedException(String message)
    {
        super(message);
    }

    public AcmOutlookModifyItemFailedException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public AcmOutlookModifyItemFailedException(Throwable cause)
    {
        super(cause);
    }

    public AcmOutlookModifyItemFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
