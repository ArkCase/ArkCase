package com.armedia.acm.plugins.complaint.exception;

/**
 * Created by armdev on 6/4/14.
 */
public class AcmComplaintException extends Exception
{
    public AcmComplaintException()
    {
    }

    public AcmComplaintException(String message)
    {
        super(message);
    }

    public AcmComplaintException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public AcmComplaintException(Throwable cause)
    {
        super(cause);
    }

    public AcmComplaintException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
