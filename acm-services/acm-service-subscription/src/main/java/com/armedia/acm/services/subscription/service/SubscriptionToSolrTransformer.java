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
public class SubscriptionToSolrTransformer implements AcmObjectToSolrDocTransformer<AcmSubscription> {

    private SubscriptionDao subscriptiontDao;

    @Override
    public List<AcmSubscription> getObjectsModifiedSince(Date lastModified, int start, int pageSize) {
        return getSubscriptiontDao().findModifiedSince(lastModified,start,pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(AcmSubscription in) {
        // No implementation needed  because we don't want Subscription indexed in the SolrAdvancedSearch
        return null;
    }

    @Override
    public SolrDocument toSolrQuickSearch(AcmSubscription in) {

        SolrDocument solr = new SolrDocument();

        solr.setId(in.getId() + "-"+in.getObjectType());
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
    public SolrAdvancedSearchDocument toContentFileIndex(AcmSubscription in) {
        // No implementation needed
        return null;
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType) {

        boolean objectNotNull = acmObjectType != null;
        String ourClassName = AcmSubscription.class.getName();
        String theirClassName = acmObjectType.getName();
        boolean classNames = theirClassName.equals(ourClassName);
        boolean isSupported = objectNotNull && classNames;

        return isSupported;
    }

    public SubscriptionDao getSubscriptiontDao() {
        return subscriptiontDao;
    }

    public void setSubscriptiontDao(SubscriptionDao subscriptiontDao) {
        this.subscriptiontDao = subscriptiontDao;
    }
}
