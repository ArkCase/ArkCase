package com.armedia.acm.plugins.admin.exception;

/**
 * Created by sergey.kolomiets on 6/28/15.
 */
public class AcmLinkFormsWorkflowException extends Exception{

    public AcmLinkFormsWorkflowException()
    {
    }

    public AcmLinkFormsWorkflowException(String message)
    {
        super(message);
    }

    public AcmLinkFormsWorkflowException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public AcmLinkFormsWorkflowException(Throwable cause)
    {
        super(cause);
    }

    public AcmLinkFormsWorkflowException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}



