package com.armedia.acm.service.identity.exceptions;

public class AcmIdentityNotReadyException extends AcmIdentityException
{
    public AcmIdentityNotReadyException(String s)
    {
        super(s);
    }

    public AcmIdentityNotReadyException(String msg, Exception e)
    {
        super(msg, e);
    }
}
