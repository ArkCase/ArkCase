package com.armedia.acm.services.billing.model;

/**
 * @author sasko.tanaskoski
 *
 */
public class BillingItemCreatedEvent extends BillingItemEvent
{
    private static final long serialVersionUID = -3739707385279393604L;

    public BillingItemCreatedEvent(BillingItem source)
    {
        super(source);
    }

    @Override
    public String getEventType()
    {
        return BillingConstants.BILLING_ITEM_CREATED_EVENT;
    }
}
