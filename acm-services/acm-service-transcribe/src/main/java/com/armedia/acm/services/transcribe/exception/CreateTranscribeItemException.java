package com.armedia.acm.services.transcribe.exception;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 02/27/2018
 */
public class CreateTranscribeItemException extends TranscribeException
{
    public CreateTranscribeItemException(String message)
    {
        super(message);
    }

    public CreateTranscribeItemException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public CreateTranscribeItemException(Throwable cause)
    {
        super(cause);
    }

    protected CreateTranscribeItemException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
