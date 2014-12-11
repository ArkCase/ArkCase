package com.armedia.acm.plugins.personnelsecurity.correspondence.service.exception;

/**
 * Created by marjan.stefanoski on 11.12.2014.
 */
public class AcmPersonnelSecurityCorrespondenceException extends  Exception{

    public AcmPersonnelSecurityCorrespondenceException() {}
    public AcmPersonnelSecurityCorrespondenceException(String message)
    {
        super(message);
    }
    public AcmPersonnelSecurityCorrespondenceException(String message, Throwable cause)
    {
        super(message, cause);
    }
    public AcmPersonnelSecurityCorrespondenceException(Throwable cause)
    {
        super(cause);
    }
    public AcmPersonnelSecurityCorrespondenceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
