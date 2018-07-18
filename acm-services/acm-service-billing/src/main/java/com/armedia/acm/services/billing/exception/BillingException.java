package com.armedia.acm.services.billing.exception;

/**
 * @author sasko.tanaskoski
 *
 */
public class BillingException extends Exception
{

    private static final long serialVersionUID = -5289789016777364683L;

    public BillingException(String message)
    {
        super(message);
    }

    public BillingException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public BillingException(Throwable cause)
    {
        super(cause);
    }

    protected BillingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
