package com.armedia.acm.plugins.admin.exception;

/**
 * Created by admin on 2/14/16.
 */
public class AcmLabelManagementException extends Exception{

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


