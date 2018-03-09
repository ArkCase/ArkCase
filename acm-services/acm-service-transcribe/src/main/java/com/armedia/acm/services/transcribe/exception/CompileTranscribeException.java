package com.armedia.acm.services.transcribe.exception;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 02/28/2018
 */
public class CompileTranscribeException extends TranscribeException
{
    public CompileTranscribeException(String message)
    {
        super(message);
    }

    public CompileTranscribeException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public CompileTranscribeException(Throwable cause)
    {
        super(cause);
    }

    protected CompileTranscribeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
