package com.armedia.acm.service.identifier.exceptions;

public class AcmIdentityException extends Exception
{
    public AcmIdentityException(String s)
    {
        super(s);
    }

    public AcmIdentityException(String msg, Exception e)
    {
        super(msg, e);
    }
}
