package com.armedia.acm.services.transcribe.exception;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/12/2018
 */
public class TranscribeServiceProviderNotFoundException extends TranscribeException
{
    public TranscribeServiceProviderNotFoundException(String message)
    {
        super(message);
    }

    public TranscribeServiceProviderNotFoundException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public TranscribeServiceProviderNotFoundException(Throwable cause)
    {
        super(cause);
    }

    protected TranscribeServiceProviderNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
