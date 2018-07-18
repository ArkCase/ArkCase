package com.armedia.acm.services.billing.exception;

/**
 * @author sasko.tanaskoski
 *
 */
public class GetBillingItemException extends BillingException
{

    private static final long serialVersionUID = -4661071457896389632L;

    public GetBillingItemException(String message)
    {
        super(message);
    }

    public GetBillingItemException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public GetBillingItemException(Throwable cause)
    {
        super(cause);
    }

    public GetBillingItemException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
