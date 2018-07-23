package com.armedia.acm.services.billing.transformer;

import com.armedia.acm.services.billing.dao.BillingInvoiceDao;
import com.armedia.acm.services.billing.model.BillingConstants;
import com.armedia.acm.services.billing.model.BillingInvoice;
import com.armedia.acm.services.billing.model.BillingItem;
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

        /** Additional properties for full names instead of ID's */
        AcmUser creator = getUserDao().quietFindByUserId(in.getCreator());
        if (creator != null)
        {
            solr.setAdditionalProperty("creator_full_name_lcs", creator.getFirstName() + " " + creator.getLastName());
        }

        solr.setAdditionalProperty("invoice_number_lcs", in.getInvoiceNumber());
        solr.setAdditionalProperty("invoice_paid_flag_b", in.getInvoicePaidFlag());
        solr.setAdditionalProperty("invoice_billing_items_lcs", new Gson().toJson(in.getBillingItems()));

        solr.setAdditionalProperty("parent_object_type_s", in.getParentObjectType());
        solr.setAdditionalProperty("parent_object_id_i", in.getParentObjectId());
        solr.setParent_ref_s(String.format("%d-%s", in.getParentObjectId(), in.getParentObjectType()));

        return solr;
    }

    @Override
    public SolrDocument toSolrQuickSearch(BillingInvoice in)
    {
        SolrDocument solrDoc = new SolrDocument();
        solrDoc.setId(String.format("%d-%s", in.getId(), BillingConstants.OBJECT_TYPE_ITEM));
        solrDoc.setObject_type_s(BillingConstants.OBJECT_TYPE_ITEM);
        solrDoc.setName(String.format("%s_%d", BillingConstants.OBJECT_TYPE_ITEM, in.getId()));
        solrDoc.setObject_id_s(in.getId() + "");
        solrDoc.setCreate_tdt(in.getCreated());
        solrDoc.setAdditionalProperty("parent_object_type_s", in.getParentObjectType());
        solrDoc.setAdditionalProperty("parent_object_id_i", in.getParentObjectId());
        solrDoc.setParent_ref_s(String.format("%d-%s", in.getParentObjectId(), in.getParentObjectType()));
        solrDoc.setAdditionalProperty("creator_s", in.getCreator());

        solrDoc.setAdditionalProperty("invoice_number_lcs", in.getInvoiceNumber());
        solrDoc.setAdditionalProperty("invoice_paid_flag_b", in.getInvoicePaidFlag());

        return solrDoc;
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return BillingItem.class.equals(acmObjectType);
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
        return BillingItem.class;
    }
}
