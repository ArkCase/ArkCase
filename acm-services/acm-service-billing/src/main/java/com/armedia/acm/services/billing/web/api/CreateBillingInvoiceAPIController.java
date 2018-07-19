package com.armedia.acm.services.billing.web.api;

import com.armedia.acm.services.billing.exception.CreateBillingInvoiceException;
import com.armedia.acm.services.billing.model.BillingInvoice;
import com.armedia.acm.services.billing.service.BillingService;

import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

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
@Controller
@RequestMapping({ "/api/v1/plugin/billing", "/api/latest/plugin/billing" })
public class CreateBillingInvoiceAPIController
{
    BillingService billingService;

    // @PreAuthorize("hasPermission(#billingItem.parentObjectId, #billingItem.parentObjectType,
    // 'createBillingInvoice')")
    @RequestMapping(value = "/invoices", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public BillingInvoice createBillingInvoice(
            @RequestBody String jsonObject, Authentication authentication, HttpSession httpSession)
            throws CreateBillingInvoiceException
    {
        JSONObject parentObject = new JSONObject(jsonObject);
        String parentObjectType = parentObject.get("parentObjectType").toString();
        Long parentObjectId = Long.parseLong(parentObject.get("parentObjectId").toString());
        return getBillingService().createBillingInvoice(parentObjectType, parentObjectId);
    }

    /**
     * @return the billingService
     */
    public BillingService getBillingService()
    {
        return billingService;
    }

    /**
     * @param billingService
     *            the billingService to set
     */
    public void setBillingService(BillingService billingService)
    {
        this.billingService = billingService;
    }
}
