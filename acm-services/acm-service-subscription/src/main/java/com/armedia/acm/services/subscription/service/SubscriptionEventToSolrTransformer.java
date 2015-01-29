package com.armedia.acm.services.subscription.service;

import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.subscription.dao.SubscriptionEventDao;
import com.armedia.acm.services.subscription.model.AcmSubscriptionEvent;

import java.util.Date;
import java.util.List;

/**
 * Created by marjan.stefanoski on 29.01.2015.
 */
public class SubscriptionEventToSolrTransformer implements AcmObjectToSolrDocTransformer<AcmSubscriptionEvent> {

    private SubscriptionEventDao subscriptionEventDao;
    private AcmPlugin subscriptionEventPlugin;

    @Override
    public List<AcmSubscriptionEvent> getObjectsModifiedSince(Date lastModified, int start, int pageSize) {
        return getSubscriptionEventDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(AcmSubscriptionEvent in) {

        SolrAdvancedSearchDocument solr = new SolrAdvancedSearchDocument();

        solr.setId(in.getId() + "-"+in.getObjectType());
        solr.setObject_id_s(in.getId() + "");
        solr.setObject_type_s(in.getObjectType());

        solr.setCreate_date_tdt(in.getCreated());
        solr.setCreator_lcs(in.getCreator());
        solr.setModified_date_tdt(in.getModified());
        solr.setModifier_lcs(in.getModifier());

        solr.setTitle_parseable((String)getSubscriptionEventPlugin().getPluginProperties().get(in.getEventType()));

        solr.setParent_id_s(Long.toString(in.getEventObjectId()));
        solr.setParent_type_s(in.getEventObjectType());
        solr.setParent_name_t(in.getEventObjectName());
        solr.setParent_number_lcs(in.getEventObjectNumber());

        solr.setOwner_lcs(in.getEventSubscriptionOwner());

        return solr;
    }

    // No implementation needed  because we don't want SubscriptionEvent indexed in the SolrQuickSearch
    @Override
    public SolrDocument toSolrQuickSearch(AcmSubscriptionEvent in) {
        return null;
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType) {

        boolean objectNotNull = acmObjectType != null;
        String ourClassName = AcmSubscriptionEvent.class.getName();
        String theirClassName = acmObjectType.getName();
        boolean classNames = theirClassName.equals(ourClassName);
        boolean isSupported = objectNotNull && classNames;

        return isSupported;
    }

    public SubscriptionEventDao getSubscriptionEventDao() {
        return subscriptionEventDao;
    }

    public void setSubscriptionEventDao(SubscriptionEventDao subscriptionEventDao) {
        this.subscriptionEventDao = subscriptionEventDao;
    }

    public AcmPlugin getSubscriptionEventPlugin() {
        return subscriptionEventPlugin;
    }

    public void setSubscriptionEventPlugin(AcmPlugin subscriptionEventPlugin) {
        this.subscriptionEventPlugin = subscriptionEventPlugin;
    }
}
