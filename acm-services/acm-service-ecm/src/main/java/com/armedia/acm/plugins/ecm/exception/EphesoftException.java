package com.armedia.acm.plugins.ecm.exception;

/**
 * Created by joseph.mcgrady on 9/18/2015.
 */
public class EphesoftException extends Exception {

    public EphesoftException() {}

    public EphesoftException(String message)
    {
        super(message);
    }

    public EphesoftException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public EphesoftException(Throwable cause)
    {
        super(cause);
    }

    public EphesoftException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}