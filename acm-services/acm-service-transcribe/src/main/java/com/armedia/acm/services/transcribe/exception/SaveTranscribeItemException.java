package com.armedia.acm.services.transcribe.exception;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 02/27/2018
 */
public class SaveTranscribeItemException extends TranscribeException
{
    public SaveTranscribeItemException(String message)
    {
        super(message);
    }

    public SaveTranscribeItemException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public SaveTranscribeItemException(Throwable cause)
    {
        super(cause);
    }

    protected SaveTranscribeItemException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
