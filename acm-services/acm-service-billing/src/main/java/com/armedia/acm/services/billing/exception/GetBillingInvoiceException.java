package com.armedia.acm.services.billing.exception;

/**
 * @author sasko.tanaskoski
 *
 */
public class GetBillingInvoiceException extends BillingException
{

    private static final long serialVersionUID = 8567114299075536078L;

    public GetBillingInvoiceException(String message)
    {
        super(message);
    }

    public GetBillingInvoiceException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public GetBillingInvoiceException(Throwable cause)
    {
        super(cause);
    }

    public GetBillingInvoiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
