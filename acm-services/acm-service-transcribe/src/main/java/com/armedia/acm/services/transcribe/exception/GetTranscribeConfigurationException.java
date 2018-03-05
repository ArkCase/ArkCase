package com.armedia.acm.services.transcribe.exception;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 02/28/2018
 */
public class GetTranscribeConfigurationException extends TranscribeException
{
    public GetTranscribeConfigurationException(String message)
    {
        super(message);
    }

    public GetTranscribeConfigurationException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public GetTranscribeConfigurationException(Throwable cause)
    {
        super(cause);
    }

    protected GetTranscribeConfigurationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
