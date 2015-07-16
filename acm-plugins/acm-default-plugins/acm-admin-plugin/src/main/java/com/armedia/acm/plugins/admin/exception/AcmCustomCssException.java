package com.armedia.acm.plugins.admin.exception;

/**
 * Created by sergey.kolomiets on 6/19/15.
 */
public class AcmCustomCssException extends Exception{

    public AcmCustomCssException()
    {
    }

    public AcmCustomCssException(String message)
    {
        super(message);
    }

    public AcmCustomCssException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public AcmCustomCssException(Throwable cause)
    {
        super(cause);
    }

    public AcmCustomCssException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}


