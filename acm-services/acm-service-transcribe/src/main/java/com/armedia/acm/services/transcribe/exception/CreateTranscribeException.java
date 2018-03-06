package com.armedia.acm.services.transcribe.exception;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 02/27/2018
 */
public class CreateTranscribeException extends TranscribeException
{
    public CreateTranscribeException(String message)
    {
        super(message);
    }

    public CreateTranscribeException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public CreateTranscribeException(Throwable cause)
    {
        super(cause);
    }

    protected CreateTranscribeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
