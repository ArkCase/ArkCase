package com.armedia.acm.services.billing.model;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;

/**
 * @author sasko.tanaskoski
 *
 */
public class BillingEventPublisher implements ApplicationEventPublisherAware
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private ApplicationEventPublisher applicationEventPublisher;

    public void publishBillingItemCreatedEvent(BillingItem source, Authentication authentication, String ipAddress, boolean succeeded)
    {
        String user = authentication != null && authentication.getName() != null ? authentication.getName()
                : BillingConstants.BILLING_SYSTEM_USER;

        BillingItemCreatedEvent billingItemCreatedEvent = new BillingItemCreatedEvent(source);
        billingItemCreatedEvent.setUserId(user);
        billingItemCreatedEvent.setIpAddress(ipAddress);
        billingItemCreatedEvent.setParentObjectId(source.getParentObjectId());
        billingItemCreatedEvent.setParentObjectType(source.getParentObjectType());
        billingItemCreatedEvent.setSucceeded(succeeded);

        getApplicationEventPublisher().publishEvent(billingItemCreatedEvent);
    }

    public void publishBillingInvoiceCreatedEvent(BillingInvoice source, Authentication authentication, String ipAddress, boolean succeeded)
    {
        String user = authentication != null && authentication.getName() != null ? authentication.getName()
                : BillingConstants.BILLING_SYSTEM_USER;

        BillingInvoiceCreatedEvent billingInvoiceCreatedEvent = new BillingInvoiceCreatedEvent(source);
        billingInvoiceCreatedEvent.setUserId(user);
        billingInvoiceCreatedEvent.setIpAddress(ipAddress);
        billingInvoiceCreatedEvent.setParentObjectId(source.getParentObjectId());
        billingInvoiceCreatedEvent.setParentObjectType(source.getParentObjectType());
        billingInvoiceCreatedEvent.setSucceeded(succeeded);

        getApplicationEventPublisher().publishEvent(billingInvoiceCreatedEvent);
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
