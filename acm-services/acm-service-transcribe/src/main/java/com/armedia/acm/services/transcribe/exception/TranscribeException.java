package com.armedia.acm.services.transcribe.exception;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 02/27/2018
 */
public class TranscribeException extends Exception
{
    public TranscribeException(String message)
    {
        super(message);
    }

    public TranscribeException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public TranscribeException(Throwable cause)
    {
        super(cause);
    }

    protected TranscribeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
