package com.armedia.acm.services.billing.service;

import com.armedia.acm.services.billing.exception.CreateBillingInvoiceException;
import com.armedia.acm.services.billing.exception.CreateBillingItemException;
import com.armedia.acm.services.billing.exception.GetBillingInvoiceException;
import com.armedia.acm.services.billing.exception.GetBillingItemException;
import com.armedia.acm.services.billing.model.BillingInvoice;
import com.armedia.acm.services.billing.model.BillingInvoiceRequest;
import com.armedia.acm.services.billing.model.BillingItem;

import java.util.List;

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
public interface BillingService
{
    List<BillingItem> getBillingItemsByParentObjectTypeAndId(String parentObjectType, Long parentObjectId)
            throws GetBillingItemException;

    BillingItem createBillingItem(BillingItem billingItem) throws CreateBillingItemException;

    List<BillingInvoice> getBillingInvoicesByParentObjectTypeAndId(String parentObjectType, Long parentObjectId)
            throws GetBillingInvoiceException;

    BillingInvoice getLatestBillingInvoice(String parentObjectType, Long parentObjectId)
            throws GetBillingInvoiceException;

    BillingInvoice createBillingInvoice(BillingInvoiceRequest billingInvoiceRequest)
            throws CreateBillingInvoiceException;
}
