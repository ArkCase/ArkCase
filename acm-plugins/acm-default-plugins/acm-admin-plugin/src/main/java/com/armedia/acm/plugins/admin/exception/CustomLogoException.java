package com.armedia.acm.plugins.admin.exception;

/**
 * Created by sergey.kolomiets on 6/19/15.
 */
public class CustomLogoException extends Exception{

    public CustomLogoException()
    {
    }

    public CustomLogoException(String message)
    {
        super(message);
    }

    public CustomLogoException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public CustomLogoException(Throwable cause)
    {
        super(cause);
    }

    public CustomLogoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}


