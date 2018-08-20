package com.armedia.acm.services.billing.transformer;

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
import com.armedia.acm.services.billing.dao.BillingInvoiceDao;
import com.armedia.acm.services.billing.model.BillingConstants;
import com.armedia.acm.services.billing.model.BillingInvoice;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.google.gson.Gson;

import java.util.Date;
import java.util.List;

/**
 * @author sasko.tanaskoski
 *
 */
public class BillingInvoiceToSolrTransformer implements AcmObjectToSolrDocTransformer<BillingInvoice>
{

    private UserDao userDao;
    private BillingInvoiceDao billingInvoiceDao;

    @Override
    public List<BillingInvoice> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getBillingInvoiceDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(BillingInvoice in)
    {
        SolrAdvancedSearchDocument solr = new SolrAdvancedSearchDocument();

        solr.setId(String.format("%d-%s", in.getId(), BillingConstants.OBJECT_TYPE_INVOICE));

        solr.setObject_id_s(in.getId() + "");
        solr.setObject_type_s(BillingConstants.OBJECT_TYPE_INVOICE);

        solr.setName(String.format("%s_%d", BillingConstants.OBJECT_TYPE_INVOICE, in.getId()));

        solr.setCreate_date_tdt(in.getCreated());
        solr.setCreator_lcs(in.getCreator());
        solr.setModified_date_tdt(in.getModified());

        /** Additional properties for full names instead of ID's */
        AcmUser creator = getUserDao().quietFindByUserId(in.getCreator());
        if (creator != null)
        {
            solr.setAdditionalProperty("creator_full_name_lcs", creator.getFirstName() + " " + creator.getLastName());
        }

        AcmUser modifier = getUserDao().quietFindByUserId(in.getModifier());
        if (modifier != null)
        {
            solr.setAdditionalProperty("modifier_full_name_lcs", modifier.getFirstName() + " " + modifier.getLastName());
        }

        solr.setAdditionalProperty("invoice_number_lcs", in.getInvoiceNumber());
        solr.setAdditionalProperty("invoice_paid_flag_b", in.getInvoicePaidFlag());
        solr.setAdditionalProperty("invoice_billing_items_lcs", new Gson().toJson(in.getBillingItems()));

        solr.setAdditionalProperty("parent_object_type_s", in.getParentObjectType());
        solr.setAdditionalProperty("parent_object_id_i", in.getParentObjectId());
        solr.setAdditionalProperty("invoice_ecm_file_id_i", in.getBillingInvoiceEcmFile().getId());
        solr.setParent_ref_s(String.format("%d-%s", in.getParentObjectId(), in.getParentObjectType()));

        return solr;
    }

    @Override
    public SolrDocument toSolrQuickSearch(BillingInvoice in)
    {
        SolrDocument solrDoc = new SolrDocument();
        solrDoc.setId(String.format("%d-%s", in.getId(), BillingConstants.OBJECT_TYPE_INVOICE));
        solrDoc.setObject_type_s(BillingConstants.OBJECT_TYPE_INVOICE);
        solrDoc.setName(String.format("%s_%d", BillingConstants.OBJECT_TYPE_INVOICE, in.getId()));
        solrDoc.setObject_id_s(in.getId() + "");
        solrDoc.setCreate_tdt(in.getCreated());
        solrDoc.setAdditionalProperty("parent_object_type_s", in.getParentObjectType());
        solrDoc.setAdditionalProperty("parent_object_id_i", in.getParentObjectId());
        solrDoc.setParent_ref_s(String.format("%d-%s", in.getParentObjectId(), in.getParentObjectType()));
        solrDoc.setAdditionalProperty("creator_s", in.getCreator());

        solrDoc.setAdditionalProperty("invoice_number_lcs", in.getInvoiceNumber());
        solrDoc.setAdditionalProperty("invoice_paid_flag_b", in.getInvoicePaidFlag());
        solrDoc.setAdditionalProperty("invoice_ecm_file_id_i", in.getBillingInvoiceEcmFile().getId());

        return solrDoc;
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return BillingInvoice.class.equals(acmObjectType);
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public BillingInvoiceDao getBillingInvoiceDao()
    {
        return billingInvoiceDao;
    }

    public void setBillingInvoiceDao(BillingInvoiceDao billingInvoiceDao)
    {
        this.billingInvoiceDao = billingInvoiceDao;
    }

    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return BillingInvoice.class;
    }
}
