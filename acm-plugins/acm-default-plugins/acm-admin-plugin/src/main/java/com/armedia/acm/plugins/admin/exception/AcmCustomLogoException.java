package com.armedia.acm.plugins.admin.exception;

/**
 * Created by sergey.kolomiets on 6/19/15.
 */
public class AcmCustomLogoException extends Exception{

    public AcmCustomLogoException()
    {
    }

    public AcmCustomLogoException(String message)
    {
        super(message);
    }

    public AcmCustomLogoException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public AcmCustomLogoException(Throwable cause)
    {
        super(cause);
    }

    public AcmCustomLogoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}


