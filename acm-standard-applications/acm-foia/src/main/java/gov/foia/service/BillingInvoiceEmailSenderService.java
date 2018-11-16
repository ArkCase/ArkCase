package gov.foia.service;

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

import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.services.billing.exception.GetBillingInvoiceException;
import com.armedia.acm.services.billing.model.BillingConstants;
import com.armedia.acm.services.billing.model.BillingInvoice;
import com.armedia.acm.services.billing.model.BillingInvoiceRequest;
import com.armedia.acm.services.billing.service.BillingService;
import com.armedia.acm.services.users.model.AcmUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import gov.foia.model.FOIARequest;
import gov.foia.model.FOIARequestUtils;

public class BillingInvoiceEmailSenderService extends AbstractEmailSenderService
{
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private CaseFileDao caseFileDao;
    private BillingService billingService;

    public void sendBillingInvoiceByEmail(BillingInvoiceRequest billingInvoiceRequest, AcmUser acmUser, Authentication authentication)
            throws Exception
    {
        FOIARequest foiaRequest = (FOIARequest) getCaseFileDao().find(billingInvoiceRequest.getParentObjectId());

        String requesterName = FOIARequestUtils.extractRequestorName(foiaRequest.getOriginator().getPerson());
        String emailAddress = FOIARequestUtils.extractRequestorEmailAddress(foiaRequest.getOriginator().getPerson());
        BillingInvoice billingInvoice = getBillingService().getLatestBillingInvoice(billingInvoiceRequest.getParentObjectType(),
                billingInvoiceRequest.getParentObjectId());

        if (Objects.isNull(billingInvoice) || Objects.isNull(billingInvoice.getBillingInvoiceEcmFile()))
        {
            throw new GetBillingInvoiceException(String.format("Billing Invoice Not Found for [%s] [%s]",
                    billingInvoiceRequest.getParentObjectType(), billingInvoiceRequest.getParentObjectId()));
        }

        String subject = String.format(BillingConstants.BILLING_EMAIL_SUBJECT, billingInvoice.getInvoiceNumber());

        Map<String, Object> bodyModel = new HashMap<>();
        bodyModel.put("header", String.format(BillingConstants.BILLING_EMAIL_HEADER, requesterName));
        bodyModel.put("body", BillingConstants.BILLING_EMAIL_BODY);
        bodyModel.put("footer", BillingConstants.BILLING_EMAIL_FOOTER);

        log.debug(String.format("Trying to send Billing Invoice - %s", billingInvoice.getInvoiceNumber()));
        sendEmailWithAttachment(Arrays.asList(emailAddress), subject, bodyModel,
                Arrays.asList(billingInvoice.getBillingInvoiceEcmFile().getFileId()), acmUser, authentication);
    }

    public CaseFileDao getCaseFileDao()
    {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }

    public BillingService getBillingService()
    {
        return billingService;
    }

    public void setBillingService(BillingService billingService)
    {
        this.billingService = billingService;
    }
}
