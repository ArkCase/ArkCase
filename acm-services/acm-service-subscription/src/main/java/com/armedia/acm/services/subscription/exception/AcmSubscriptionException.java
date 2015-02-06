package com.armedia.acm.services.subscription.exception;

/**
 * Created by marjan.stefanoski on 03.02.2015.
 */
public class AcmSubscriptionException extends Exception {

    public AcmSubscriptionException(){}

    public AcmSubscriptionException(String message)
    {
        super(message);
    }

    public AcmSubscriptionException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public AcmSubscriptionException(Throwable cause)
    {
        super(cause);
    }

    public AcmSubscriptionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
