package com.armedia.acm.plugins.ecm.exception;

/**
 * Created by sergey.kolomiets on 18.06.2015.
 */
public class AcmFileTypesException extends Exception {

    public AcmFileTypesException() {}

    public AcmFileTypesException(String message)
    {
        super(message);
    }

    public AcmFileTypesException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public AcmFileTypesException(Throwable cause)
    {
        super(cause);
    }

    public AcmFileTypesException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
