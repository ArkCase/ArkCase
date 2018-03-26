package com.armedia.acm.services.transcribe.exception;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 02/28/2018
 */
public class SaveConfigurationException extends TranscribeException
{
    public SaveConfigurationException(String message)
    {
        super(message);
    }

    public SaveConfigurationException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public SaveConfigurationException(Throwable cause)
    {
        super(cause);
    }

    protected SaveConfigurationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
