package com.armedia.acm.plugins.admin.exception;

/**
 * Created by sergey.kolomiets on 6/19/15.
 */
public class CustomCssException extends Exception{

    public CustomCssException()
    {
    }

    public CustomCssException(String message)
    {
        super(message);
    }

    public CustomCssException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public CustomCssException(Throwable cause)
    {
        super(cause);
    }

    public CustomCssException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}


