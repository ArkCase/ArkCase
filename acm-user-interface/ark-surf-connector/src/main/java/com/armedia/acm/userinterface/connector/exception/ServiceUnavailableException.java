package com.armedia.acm.userinterface.connector.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Created by riste.tutureski on 8/6/2015.
 */
public class ServiceUnavailableException extends AuthenticationException
{

    public ServiceUnavailableException(String msg)
    {
        super(msg);
    }

    public ServiceUnavailableException(String msg, Throwable t)
    {
        super(msg, t);
    }
}