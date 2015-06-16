package com.armedia.acm.plugins.admin.exception;

/**
 * Created by admin on 6/28/15.
 */
public class AcmWorkflowConfigurationException extends Exception{

    public AcmWorkflowConfigurationException()
    {
    }

    public AcmWorkflowConfigurationException(String message)
    {
        super(message);
    }

    public AcmWorkflowConfigurationException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public AcmWorkflowConfigurationException(Throwable cause)
    {
        super(cause);
    }

    public AcmWorkflowConfigurationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}


