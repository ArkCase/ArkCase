package com.armedia.acm.plugins.complaint.web.api;

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

import com.armedia.acm.plugins.billing.service.BillingInvoiceEmailSenderService;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.service.GetComplaintServiceImpl;
import com.armedia.acm.services.billing.model.BillingInvoiceRequest;
import com.armedia.acm.services.users.model.AcmUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping({ "/api/v1/plugin/complaint/billing/invoices", "/api/latest/plugin/complaint/billing/invoices" })
public class ComplaintBillingInvoiceEmailSenderAPIController
{
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private BillingInvoiceEmailSenderService<Complaint> complaintBillingInvoiceEmailSenderService;

    private GetComplaintServiceImpl getComplaintService;

    @RequestMapping(value = "/document/email", method = RequestMethod.PUT)
    @ResponseBody
    public void sendBillingInvoiceByEmail(@RequestBody BillingInvoiceRequest billingInvoiceRequest, HttpSession session, Authentication authentication) throws Exception
    {
        AcmUser user = (AcmUser) session.getAttribute("acm_user");
        try
        {
            getComplaintBillingInvoiceEmailSenderService()
                    .setParentObject(getGetComplaintService().getComplaintById(billingInvoiceRequest.getParentObjectId()));
            getComplaintBillingInvoiceEmailSenderService().sendBillingInvoiceByEmail(billingInvoiceRequest, user, authentication);
        }
        catch (Exception e)
        {
            log.error(String.format("Could not send the Billing Invoice for [%s] [%d]", billingInvoiceRequest.getParentObjectType(), billingInvoiceRequest.getParentObjectId()), e);
            throw e;
        }
    }

    public BillingInvoiceEmailSenderService<Complaint> getComplaintBillingInvoiceEmailSenderService()
    {
        return complaintBillingInvoiceEmailSenderService;
    }

    public void setComplaintBillingInvoiceEmailSenderService(
            BillingInvoiceEmailSenderService<Complaint> complaintBillingInvoiceEmailSenderService)
    {
        this.complaintBillingInvoiceEmailSenderService = complaintBillingInvoiceEmailSenderService;
    }

    public GetComplaintServiceImpl getGetComplaintService()
    {
        return getComplaintService;
    }

    public void setGetComplaintService(GetComplaintServiceImpl getComplaintService)
    {
        this.getComplaintService = getComplaintService;
    }
}
