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

import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.CREATOR_FULL_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.MODIFIER_FULL_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_OBJECT_ID_I;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_REF_S;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_TYPE_S;

import com.armedia.acm.services.billing.dao.BillingInvoiceDao;
import com.armedia.acm.services.billing.model.BillingConstants;
import com.armedia.acm.services.billing.model.BillingInvoice;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.google.gson.Gson;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author sasko.tanaskoski
 *
 */
public class BillingInvoiceToSolrTransformer implements AcmObjectToSolrDocTransformer<BillingInvoice>
{
    private final Logger LOG = LogManager.getLogger(getClass());

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
        SolrAdvancedSearchDocument solrDoc = new SolrAdvancedSearchDocument();
        LOG.debug("Creating Solr advanced search document for BILLING_INVOICE.");

        String name = String.format("%s_%d", BillingConstants.OBJECT_TYPE_INVOICE, in.getId());
        mapRequiredProperties(solrDoc, in.getId(), in.getCreator(), in.getCreated(), in.getModifier(), in.getModified(),
                BillingConstants.OBJECT_TYPE_INVOICE, name);

        mapAdditionalProperties(in, solrDoc.getAdditionalProperties());

        return solrDoc;
    }

    @Override
    public void mapAdditionalProperties(BillingInvoice in, Map<String, Object> additionalProperties)
    {
        /** Additional properties for full names instead of ID's */
        AcmUser creator = getUserDao().quietFindByUserId(in.getCreator());
        if (creator != null)
        {
            additionalProperties.put(CREATOR_FULL_NAME_LCS, creator.getFirstName() + " " + creator.getLastName());
        }

        AcmUser modifier = getUserDao().quietFindByUserId(in.getModifier());
        if (modifier != null)
        {
            additionalProperties.put(MODIFIER_FULL_NAME_LCS, modifier.getFirstName() + " " + modifier.getLastName());
        }

        additionalProperties.put("invoice_number_lcs", in.getInvoiceNumber());
        additionalProperties.put("invoice_paid_flag_b", in.getInvoicePaidFlag());
        additionalProperties.put("invoice_billing_items_lcs", new Gson().toJson(in.getBillingItems()));

        additionalProperties.put(PARENT_TYPE_S, in.getParentObjectType());
        additionalProperties.put(PARENT_OBJECT_ID_I, in.getParentObjectId());
        additionalProperties.put("invoice_ecm_file_id_i", in.getBillingInvoiceEcmFile().getId());
        additionalProperties.put(PARENT_REF_S, String.format("%d-%s", in.getParentObjectId(), in.getParentObjectType()));
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
