package gov.foia.web.api;

import com.armedia.acm.services.billing.model.BillingInvoiceRequest;
import com.armedia.acm.services.users.model.AcmUser;
import gov.foia.service.BillingInvoiceEmailSenderService;
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
@RequestMapping({ "/api/v1/plugin/billing/invoices", "/api/latest/plugin/billing/invoices" })
public class BillingInvoiceEmailSenderAPIController
{
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private BillingInvoiceEmailSenderService billingInvoiceEmailSenderService;

    @RequestMapping(value = "/document/email", method = RequestMethod.PUT)
    @ResponseBody
    public void sendBillingInvoiceByEmail(@RequestBody BillingInvoiceRequest billingInvoiceRequest, HttpSession session, Authentication authentication) throws Exception
    {
        AcmUser user = (AcmUser) session.getAttribute("acm_user");
        try
        {
            getBillingInvoiceEmailSenderService().sendBillingInvoiceByEmail(billingInvoiceRequest, user, authentication);
        }
        catch (Exception e)
        {
            log.error(String.format("Could not send the Billing Invoice for [%s] [%d]", billingInvoiceRequest.getParentObjectType(), billingInvoiceRequest.getParentObjectId()), e);
            throw e;
        }
    }

    public BillingInvoiceEmailSenderService getBillingInvoiceEmailSenderService()
    {
        return billingInvoiceEmailSenderService;
    }

    public void setBillingInvoiceEmailSenderService(BillingInvoiceEmailSenderService billingInvoiceEmailSenderService)
    {
        this.billingInvoiceEmailSenderService = billingInvoiceEmailSenderService;
    }
}
