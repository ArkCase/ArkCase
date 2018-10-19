package gov.foia.listener;

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
