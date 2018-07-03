package com.armedia.acm.services.billing.service;

import com.armedia.acm.services.billing.dao.BillingDao;
import com.armedia.acm.services.billing.model.BillingConstants;
import com.armedia.acm.services.billing.model.BillingItem;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import java.util.Date;
import java.util.List;

public class BillingItemToSolrTransformer implements AcmObjectToSolrDocTransformer<BillingItem>
{

    private UserDao userDao;
    private BillingDao billingDao;

    @Override
    public List<BillingItem> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getBillingDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(BillingItem in)
    {
        SolrAdvancedSearchDocument solr = new SolrAdvancedSearchDocument();

        solr.setId(String.format("%d-%s", in.getId(), BillingConstants.OBJECT_TYPE));

        solr.setObject_id_s(in.getId() + "");
        solr.setObject_type_s(BillingConstants.OBJECT_TYPE);

        solr.setDescription_parseable(in.getItemDescription());
        solr.setName(String.format("%s_%d", BillingConstants.OBJECT_TYPE, in.getId()));

        solr.setCreate_date_tdt(in.getCreated());
        solr.setCreator_lcs(in.getCreator());

        /** Additional properties for full names instead of ID's */
        AcmUser creator = getUserDao().quietFindByUserId(in.getCreator());
        if (creator != null)
        {
            solr.setAdditionalProperty("creator_full_name_lcs", creator.getFirstName() + " " + creator.getLastName());
        }

        solr.setAdditionalProperty("parent_object_type_s", in.getParentObjectType());
        solr.setAdditionalProperty("parent_object_id_i", in.getParentObjectId());
        solr.setParent_ref_s(String.format("%d-%s", in.getParentObjectId(), in.getParentObjectType()));

        return solr;
    }

    @Override
    public SolrDocument toSolrQuickSearch(BillingItem in)
    {
        SolrDocument solrDoc = new SolrDocument();
        solrDoc.setId(String.format("%d-%s", in.getId(), BillingConstants.OBJECT_TYPE));
        solrDoc.setObject_type_s(BillingConstants.OBJECT_TYPE);
        solrDoc.setName(String.format("%s_%d", BillingConstants.OBJECT_TYPE, in.getId()));
        solrDoc.setObject_id_s(in.getId() + "");
        solrDoc.setCreate_tdt(in.getCreated());
        solrDoc.setAdditionalProperty("parent_object_type_s", in.getParentObjectType());
        solrDoc.setAdditionalProperty("parent_object_id_i", in.getParentObjectId());
        solrDoc.setParent_ref_s(String.format("%d-%s", in.getParentObjectId(), in.getParentObjectType()));
        solrDoc.setAdditionalProperty("creator_s", in.getCreator());

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

    public BillingDao getBillingDao()
    {
        return billingDao;
    }

    public void setBillingDao(BillingDao billingDao)
    {
        this.billingDao = billingDao;
    }

    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return BillingItem.class;
    }
}
