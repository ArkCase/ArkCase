package com.armedia.acm.plugins.profile.exception;

/**
 * Created by marjan.stefanoski on 16.10.2014.
 */
public class AcmProfileException extends Exception{

    public AcmProfileException() {}
    public AcmProfileException(String message)
    {
        super(message);
    }
    public AcmProfileException(String message, Throwable cause)
    {
        super(message, cause);
    }
    public AcmProfileException(Throwable cause)
    {
        super(cause);
    }
    public AcmProfileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}


