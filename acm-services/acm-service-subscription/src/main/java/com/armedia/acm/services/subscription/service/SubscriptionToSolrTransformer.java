package com.armedia.acm.services.subscription.service;

import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.subscription.dao.SubscriptionDao;
import com.armedia.acm.services.subscription.model.AcmSubscription;

import java.util.Date;
import java.util.List;

/**
 * Created by marjan.stefanoski on 11.03.2015.
 */
public class SubscriptionToSolrTransformer implements AcmObjectToSolrDocTransformer<AcmSubscription>
{

    private SubscriptionDao subscriptionDao;

    @Override
    public List<AcmSubscription> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getSubscriptionDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(AcmSubscription in)
    {

        SolrAdvancedSearchDocument doc = new SolrAdvancedSearchDocument();
        doc.setId(String.format("%s-%s", in.getId(), in.getObjectType()));
        doc.setObject_id_s(Long.toString(in.getId()));
        doc.setObject_type_s(in.getObjectType());

        doc.setCreate_date_tdt(in.getCreated());
        doc.setCreator_lcs(in.getCreator());
        doc.setModified_date_tdt(in.getModified());
        doc.setModifier_lcs(in.getModifier());

        doc.setParent_id_s(Long.toString(in.getObjectId()));
        doc.setParent_type_s(in.getSubscriptionObjectType());
        doc.setOwner_lcs(in.getUserId());

        return doc;
    }

    @Override
    public SolrDocument toSolrQuickSearch(AcmSubscription in)
    {

        SolrDocument solr = new SolrDocument();

        solr.setId(in.getId() + "-" + in.getObjectType());
        solr.setObject_id_s(in.getId() + "");
        solr.setObject_type_s(in.getObjectType());

        solr.setCreate_tdt(in.getCreated());
        solr.setAuthor(in.getCreator());
        solr.setLast_modified_tdt(in.getModified());
        solr.setModifier_s(in.getModifier());

        solr.setParent_object_id_s(Long.toString(in.getObjectId()));

        solr.setParent_object_type_s(in.getSubscriptionObjectType());

        solr.setOwner_s(in.getUserId());

        return solr;
    }

    @Override
    public SolrAdvancedSearchDocument toContentFileIndex(AcmSubscription in)
    {
        // No implementation needed
        return null;
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return AcmSubscription.class.equals(acmObjectType);
    }

    public SubscriptionDao getSubscriptionDao()
    {
        return subscriptionDao;
    }

    public void setSubscriptionDao(SubscriptionDao subscriptionDao)
    {
        this.subscriptionDao = subscriptionDao;
    }

    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return AcmSubscription.class;
    }
}
