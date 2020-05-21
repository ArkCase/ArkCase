package com.armedia.acm.plugins.consultation.web.api;

/*-
 * #%L
 * ACM Default Plugin: Consultation
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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
import com.armedia.acm.plugins.consultation.service.ConsultationService;
import com.armedia.acm.services.billing.model.BillingInvoiceRequest;
import com.armedia.acm.services.users.model.AcmUser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on May, 2020
 */
@Controller
@RequestMapping({ "/api/v1/plugin/consultation/billing/invoices", "/api/latest/plugin/consultation/billing/invoices" })
public class ConsultationBillingInvoiceEmailSenderAPIController
{
    private final Logger log = LogManager.getLogger(this.getClass());

    private BillingInvoiceEmailSenderService consultationBillingInvoiceEmailSenderService;

    private ConsultationService consultationService;

    @RequestMapping(value = "/document/email", method = RequestMethod.PUT)
    @ResponseBody
    public void sendBillingInvoiceByEmail(@RequestBody BillingInvoiceRequest billingInvoiceRequest, HttpSession session, Authentication authentication) throws Exception
    {
        AcmUser user = (AcmUser) session.getAttribute("acm_user");
        try
        {
            getConsultationBillingInvoiceEmailSenderService()
                    .setParentObject(getConsultationService().getConsultationById(billingInvoiceRequest.getParentObjectId()));
            getConsultationBillingInvoiceEmailSenderService().sendBillingInvoiceByEmail(billingInvoiceRequest, user, authentication);
        }
        catch (Exception e)
        {
            log.error(String.format("Could not send the Billing Invoice for [%s] [%d]", billingInvoiceRequest.getParentObjectType(), billingInvoiceRequest.getParentObjectId()), e);
            throw e;
        }
    }

    public BillingInvoiceEmailSenderService getConsultationBillingInvoiceEmailSenderService() {
        return consultationBillingInvoiceEmailSenderService;
    }

    public void setConsultationBillingInvoiceEmailSenderService(BillingInvoiceEmailSenderService consultationBillingInvoiceEmailSenderService) {
        this.consultationBillingInvoiceEmailSenderService = consultationBillingInvoiceEmailSenderService;
    }

    public ConsultationService getConsultationService() {
        return consultationService;
    }

    public void setConsultationService(ConsultationService consultationService) {
        this.consultationService = consultationService;
    }
}
