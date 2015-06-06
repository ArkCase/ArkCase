package com.armedia.acm.plugins.admin.exception;

/**
 * Created by admin on 4/28/15.
 */
public class AcmRolesPrivilegesException extends Exception {

    public AcmRolesPrivilegesException()
    {
    }

    public AcmRolesPrivilegesException(String message)
    {
        super(message);
    }

    public AcmRolesPrivilegesException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public AcmRolesPrivilegesException(Throwable cause)
    {
        super(cause);
    }

    public AcmRolesPrivilegesException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}


