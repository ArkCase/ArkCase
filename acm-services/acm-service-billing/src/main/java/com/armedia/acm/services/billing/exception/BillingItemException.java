package com.armedia.acm.services.billing.exception;

/**
 * @author sasko.tanaskoski
 *
 */
public class BillingItemException extends Exception
{

    public BillingItemException()
    {
    }

    public BillingItemException(String message)
    {
        super(message);
    }

    public BillingItemException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public BillingItemException(Throwable cause)
    {
        super(cause);
    }

    public BillingItemException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
