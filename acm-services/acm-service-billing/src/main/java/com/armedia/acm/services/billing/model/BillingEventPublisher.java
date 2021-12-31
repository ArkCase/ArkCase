package com.armedia.acm.services.billing.model;

import com.armedia.acm.auth.AuthenticationUtils;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

/*-
 * #%L
 * ACM Service: Billing
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software.
 *
 * If the software was purchased under a paid ArkCase license, the terms of
 * the paid license agreement will prevail.  Otherwise, the software is
 * provided under the following open source license terms:
 *
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

/**
 * @author sasko.tanaskoski
 *
 */
public class BillingEventPublisher implements ApplicationEventPublisherAware
{
    private transient final Logger log = LogManager.getLogger(getClass());
    private ApplicationEventPublisher applicationEventPublisher;

    public void publishBillingItemCreatedEvent(BillingItem source)
    {
        BillingItemCreatedEvent billingItemCreatedEvent = new BillingItemCreatedEvent(source);
        billingItemCreatedEvent.setUserId(AuthenticationUtils.getUsername());
        billingItemCreatedEvent.setIpAddress(AuthenticationUtils.getUserIpAddress());
        billingItemCreatedEvent.setParentObjectId(source.getParentObjectId());
        billingItemCreatedEvent.setParentObjectType(source.getParentObjectType());
        billingItemCreatedEvent.setSucceeded(true);
        getApplicationEventPublisher().publishEvent(billingItemCreatedEvent);
    }

    public void publishBillingInvoiceCreatedEvent(BillingInvoice source)
    {
        BillingInvoiceCreatedEvent billingInvoiceCreatedEvent = new BillingInvoiceCreatedEvent(source);
        billingInvoiceCreatedEvent.setUserId(AuthenticationUtils.getUsername());
        billingInvoiceCreatedEvent.setIpAddress(AuthenticationUtils.getUserIpAddress());
        billingInvoiceCreatedEvent.setParentObjectId(source.getParentObjectId());
        billingInvoiceCreatedEvent.setParentObjectType(source.getParentObjectType());
        billingInvoiceCreatedEvent.setSucceeded(true);

        getApplicationEventPublisher().publishEvent(billingInvoiceCreatedEvent);
    }

    public void publishTouchnetPaymentCreatedEvent(BillingInvoice source)
    {
        TouchnetPaymentCreatedEvent touchnetPaymentCreatedEvent = new TouchnetPaymentCreatedEvent(source);
        touchnetPaymentCreatedEvent.setUserId("TOUCHNET-PAYMENT");
        touchnetPaymentCreatedEvent.setIpAddress("");
        touchnetPaymentCreatedEvent.setParentObjectId(source.getParentObjectId());
        touchnetPaymentCreatedEvent.setParentObjectType(source.getParentObjectType());
        touchnetPaymentCreatedEvent.setSucceeded(true);

        getApplicationEventPublisher().publishEvent(touchnetPaymentCreatedEvent);
    }

    public ApplicationEventPublisher getApplicationEventPublisher()
    {
        return applicationEventPublisher;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }

}
