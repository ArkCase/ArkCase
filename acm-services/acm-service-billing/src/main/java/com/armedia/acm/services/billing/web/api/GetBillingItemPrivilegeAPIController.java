package com.armedia.acm.services.billing.web.api;

import com.armedia.acm.services.billing.service.BillingService;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.Map;

/**
 * @author aleksandar.bujaroski
 */
@Controller
@RequestMapping({ "/api/v1/plugin/billing/billingItemPrivilege", "/api/latest/plugin/billing/billingItemPrivilege" })
public class GetBillingItemPrivilegeAPIController
{
    private BillingService billingService;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Boolean> getBillingItemPrivilege(Authentication auth)
    {
       boolean hasBillingItemPrivilege = getBillingService().hasListBillingItemPrivilege(auth.getName());
       return Collections.singletonMap("billingItemPrivilege", hasBillingItemPrivilege);
    }

    public BillingService getBillingService() {
        return billingService;
    }

    public void setBillingService(BillingService billingService) {
        this.billingService = billingService;
    }
}
