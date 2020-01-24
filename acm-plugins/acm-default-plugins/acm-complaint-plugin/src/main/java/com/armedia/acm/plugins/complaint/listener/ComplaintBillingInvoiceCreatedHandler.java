package com.armedia.acm.plugins.complaint.listener;

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

import com.armedia.acm.plugins.billing.service.BillingInvoiceDocumentGenerator;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.ComplaintConstants;
import com.armedia.acm.services.billing.model.BillingInvoiceCreatedEvent;

import org.springframework.context.ApplicationListener;

/**
 * @author sasko.tanaskoski
 *
 */
public class ComplaintBillingInvoiceCreatedHandler implements ApplicationListener<BillingInvoiceCreatedEvent>
{

    private BillingInvoiceDocumentGenerator complaintBillingInvoiceDocumentGenerator;
    private ComplaintDao complaintDao;

    @Override
    public void onApplicationEvent(BillingInvoiceCreatedEvent event)
    {
        if(event.getParentObjectType().equals(ComplaintConstants.OBJECT_TYPE))
        {
            Complaint complaint = getComplaintDao().find(event.getParentObjectId());
            getComplaintBillingInvoiceDocumentGenerator().setParentObject(complaint);
            getComplaintBillingInvoiceDocumentGenerator().generatePdf(event.getParentObjectType(), event.getParentObjectId());
        }
    }

    public BillingInvoiceDocumentGenerator getComplaintBillingInvoiceDocumentGenerator()
    {
        return complaintBillingInvoiceDocumentGenerator;
    }

    public void setComplaintBillingInvoiceDocumentGenerator(
            BillingInvoiceDocumentGenerator complaintBillingInvoiceDocumentGenerator)
    {
        this.complaintBillingInvoiceDocumentGenerator = complaintBillingInvoiceDocumentGenerator;
    }

    public ComplaintDao getComplaintDao()
    {
        return complaintDao;
    }

    public void setComplaintDao(ComplaintDao complaintDao)
    {
        this.complaintDao = complaintDao;
    }
}
