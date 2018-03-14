package com.armedia.acm.services.transcribe.exception;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 02/28/2018
 */
public class GetConfigurationException extends TranscribeException
{
    public GetConfigurationException(String message)
    {
        super(message);
    }

    public GetConfigurationException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public GetConfigurationException(Throwable cause)
    {
        super(cause);
    }

    protected GetConfigurationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
