package com.armedia.acm.services.billing.model;

import com.armedia.acm.core.model.AcmEvent;

/**
 * @author sasko.tanaskoski
 *
 */
public class BillingItemEvent extends AcmEvent
{

    public BillingItemEvent(BillingItem source, String billingItemEvent, boolean succeeded, String ipAddress)
    {
        super(source);

        setObjectId(source.getId());
        setEventDate(source.getCreated());
        setUserId(source.getCreator());
        setEventType(String.format("%s.%s", BillingConstants.EVENT_TYPE, billingItemEvent));
        setSucceeded(succeeded);
        setIpAddress(ipAddress);
        setObjectType(BillingConstants.OBJECT_TYPE);
        setParentObjectId(source.getParentObjectId());
        setParentObjectType(source.getParentObjectType());
    }
}
