package com.armedia.acm.services.labels.exception;

/**
 * Created by admin on 2/14/16.
 */
public class AcmLabelManagementException extends Exception
{

    private static final long serialVersionUID = 1L;

    public AcmLabelManagementException()
    {
    }

    public AcmLabelManagementException(String message)
    {
        super(message);
    }

    public AcmLabelManagementException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public AcmLabelManagementException(Throwable cause)
    {
        super(cause);
    }

    public AcmLabelManagementException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
