package gov.foia.listener;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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

import com.armedia.acm.services.billing.model.BillingInvoiceCreatedEvent;

import org.springframework.context.ApplicationListener;

import gov.foia.service.BillingInvoiceDocumentGenerator;

/**
 * @author sasko.tanaskoski
 *
 */
public class BillingInvoiceCreatedHandler implements ApplicationListener<BillingInvoiceCreatedEvent>
{

    private BillingInvoiceDocumentGenerator billingInvoiceDocumentGenerator;

    @Override
    public void onApplicationEvent(BillingInvoiceCreatedEvent event)
    {
        getBillingInvoiceDocumentGenerator().generatePdf(event.getParentObjectType(), event.getParentObjectId());
    }

    /**
     * @return the billingInvoiceDocumentGenerator
     */
    public BillingInvoiceDocumentGenerator getBillingInvoiceDocumentGenerator()
    {
        return billingInvoiceDocumentGenerator;
    }

    /**
     * @param billingInvoiceDocumentGenerator
     *            the billingInvoiceDocumentGenerator to set
     */
    public void setBillingInvoiceDocumentGenerator(BillingInvoiceDocumentGenerator billingInvoiceDocumentGenerator)
    {
        this.billingInvoiceDocumentGenerator = billingInvoiceDocumentGenerator;
    }

}
