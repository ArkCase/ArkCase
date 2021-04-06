package com.armedia.acm.services.billing.model;

public class TouchnetPaymentCreatedEvent extends BillingInvoiceEvent
{

    private static final long serialVersionUID = -1835836489068626931L;

    public TouchnetPaymentCreatedEvent(BillingInvoice source)
    {
        super(source);
    }

    @Override
    public String getEventType()
    {
        return BillingConstants.TOUCHNET_PAYMENT_CREATED_EVENT;
    }
}
