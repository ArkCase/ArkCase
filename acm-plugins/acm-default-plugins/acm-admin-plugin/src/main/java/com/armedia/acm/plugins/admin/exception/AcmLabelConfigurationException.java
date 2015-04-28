package com.armedia.acm.plugins.admin.exception;

/**
 * Created by admin on 4/28/15.
 */
public class AcmLabelConfigurationException extends Exception{

    public AcmLabelConfigurationException()
    {
    }

    public AcmLabelConfigurationException(String message)
    {
        super(message);
    }

    public AcmLabelConfigurationException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public AcmLabelConfigurationException(Throwable cause)
    {
        super(cause);
    }

    public AcmLabelConfigurationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}


