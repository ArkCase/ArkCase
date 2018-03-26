package com.armedia.acm.auth.okta.exceptions;

public class OktaException extends Exception
{
    public OktaException()
    {
        super();
    }

    public OktaException(String message)
    {
        super(message);
    }

    public OktaException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public OktaException(Throwable cause)
    {
        super(cause);
    }
}
