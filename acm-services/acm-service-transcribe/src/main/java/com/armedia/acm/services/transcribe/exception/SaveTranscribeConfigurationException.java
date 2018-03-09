package com.armedia.acm.services.transcribe.exception;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 02/28/2018
 */
public class SaveTranscribeConfigurationException extends TranscribeException
{
    public SaveTranscribeConfigurationException(String message)
    {
        super(message);
    }

    public SaveTranscribeConfigurationException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public SaveTranscribeConfigurationException(Throwable cause)
    {
        super(cause);
    }

    protected SaveTranscribeConfigurationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
