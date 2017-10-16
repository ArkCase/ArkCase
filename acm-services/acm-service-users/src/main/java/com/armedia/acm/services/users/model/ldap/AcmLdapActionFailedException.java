package com.armedia.acm.services.users.model.ldap;


public class AcmLdapActionFailedException extends Exception
{
    public AcmLdapActionFailedException(String message)
    {
        super(message);
    }

    public AcmLdapActionFailedException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
