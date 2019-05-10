package com.armedia.acm.plugins.casefile.listener;

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
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.services.billing.model.BillingInvoiceCreatedEvent;

import org.springframework.context.ApplicationListener;

/**
 * @author sasko.tanaskoski
 *
 */
public class CaseFileBillingInvoiceCreatedHandler implements ApplicationListener<BillingInvoiceCreatedEvent>
{

    private BillingInvoiceDocumentGenerator caseFileBillingInvoiceDocumentGenerator;
    private CaseFileDao caseFileDao;

    @Override
    public void onApplicationEvent(BillingInvoiceCreatedEvent event)
    {
        CaseFile caseFile = getCaseFileDao().find(event.getParentObjectId());
        getCaseFileBillingInvoiceDocumentGenerator().setParentObject(caseFile);
        getCaseFileBillingInvoiceDocumentGenerator().generatePdf(event.getParentObjectType(), event.getParentObjectId());
    }

    public BillingInvoiceDocumentGenerator getCaseFileBillingInvoiceDocumentGenerator()
    {
        return caseFileBillingInvoiceDocumentGenerator;
    }

    public void setCaseFileBillingInvoiceDocumentGenerator(BillingInvoiceDocumentGenerator caseFileBillingInvoiceDocumentGenerator)
    {
        this.caseFileBillingInvoiceDocumentGenerator = caseFileBillingInvoiceDocumentGenerator;
    }

    public CaseFileDao getCaseFileDao()
    {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }
}
