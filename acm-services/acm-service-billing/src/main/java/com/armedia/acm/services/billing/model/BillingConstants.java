package com.armedia.acm.services.billing.model;

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
public interface BillingConstants
{
    String OBJECT_TYPE_ITEM = "BILLING_ITEM";
    String OBJECT_TYPE_INVOICE = "BILLING_INVOICE";

    String BILLING_ITEM_CREATED_EVENT = "com.armedia.acm.billing.item.created";
    String BILLING_INVOICE_CREATED_EVENT = "com.armedia.acm.billing.invoice.created";

    // TODO '/acm-config-server-repo' is for temporal compatibility with current configuration.
    String INVOICE_DOCUMENT_STYLESHEET = System.getProperty("user.home")
            + "/.arkcase/acm/acm-config-server-repo/pdf-stylesheets/billing-invoice-document.xsl";
    String INVOICE_DOCUMENT_TYPE = "billing_invoice_document";
    String INVOICE_DOCUMENT_MIME_TYPE_PDF = "application/pdf";

    String BILLING_SYSTEM_USER = "BILLING_SERVICE";

    String BILLING_CURRENCY_FORMAT = "$%,.2f";
    String BILLING_EMAIL_SUBJECT = "FOIA Billing Invoice - %s";
    String BILLING_EMAIL_HEADER = "Dear %s";
    String BILLING_EMAIL_BODY = "Please review your request invoice attached and make payment. If you have any questions, please email or call the FOIA Office.";
    String BILLING_EMAIL_FOOTER = "FOIA Office Staff";

    String BILLING_ITEM_TYPE_TIMESHEET = "Timesheet";
    String BILLING_ITEM_TYPE_COSTSHEET = "Costsheet";
    String BILLING_ITEM_TYPE_DEFAULT = "Adhoc";
}
