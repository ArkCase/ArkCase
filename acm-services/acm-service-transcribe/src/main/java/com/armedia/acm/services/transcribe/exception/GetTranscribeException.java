package com.armedia.acm.services.transcribe.exception;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 02/27/2018
 */
public class GetTranscribeException extends TranscribeException
{
    public GetTranscribeException(String message)
    {
        super(message);
    }

    public GetTranscribeException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public GetTranscribeException(Throwable cause)
    {
        super(cause);
    }

    protected GetTranscribeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
