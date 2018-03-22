package com.armedia.acm.services.transcribe.exception;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 02/27/2018
 */
public class SaveTranscribeException extends TranscribeException
{
    public SaveTranscribeException(String message)
    {
        super(message);
    }

    public SaveTranscribeException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public SaveTranscribeException(Throwable cause)
    {
        super(cause);
    }

    protected SaveTranscribeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
