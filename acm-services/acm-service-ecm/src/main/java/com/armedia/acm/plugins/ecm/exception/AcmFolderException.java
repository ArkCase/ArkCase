package com.armedia.acm.plugins.ecm.exception;

/**
 * Created by marjan.stefanoski on 20.04.2015.
 */
public class AcmFolderException extends Exception {

    public AcmFolderException() {}

    public AcmFolderException(String message)
    {
        super(message);
    }

    public AcmFolderException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public AcmFolderException(Throwable cause)
    {
        super(cause);
    }

    public AcmFolderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
