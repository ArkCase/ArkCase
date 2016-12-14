package com.armedia.acm.services.subscription.service;

import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.subscription.dao.SubscriptionEventDao;
import com.armedia.acm.services.subscription.model.AcmSubscriptionEvent;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import java.util.Date;
import java.util.List;

/**
 * Created by marjan.stefanoski on 29.01.2015.
 */
public class SubscriptionEventToSolrTransformer implements AcmObjectToSolrDocTransformer<AcmSubscriptionEvent>
{

    private SubscriptionEventDao subscriptionEventDao;
    private AcmPlugin subscriptionEventPlugin;
    private UserDao userDao;

    @Override
    public List<AcmSubscriptionEvent> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getSubscriptionEventDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(AcmSubscriptionEvent in)
    {

        SolrAdvancedSearchDocument solr = new SolrAdvancedSearchDocument();

        solr.setId(in.getId() + "-" + in.getObjectType());
        solr.setObject_id_s(in.getId() + "");
        solr.setObject_type_s(in.getObjectType());

        solr.setCreate_date_tdt(in.getCreated());
        solr.setCreator_lcs(in.getCreator());
        solr.setModified_date_tdt(in.getModified());
        solr.setModifier_lcs(in.getModifier());

        String title;
        if (in.getEventType() != null && getSubscriptionEventPlugin().getPluginProperties().containsKey(in.getEventType()))
        {
            title = (String) getSubscriptionEventPlugin().getPluginProperties().get(in.getEventType());
        } else if (in.getEventType() != null)
        {
            title = "Subscription on " + in.getEventObjectType() + ":" + in.getEventObjectId() + " - " + in.getEventObjectName();
        } else
        {
            title = "Subscription on " + in.getEventObjectType() + ":" + in.getEventObjectId() + " - " + in.getEventObjectName();
        }
        solr.setTitle_parseable(title);

        if (in.getEventObjectId() != null)
        {
            solr.setParent_id_s(Long.toString(in.getEventObjectId()));
        } else
        {
            solr.setParent_id_s("");
        }

        solr.setParent_type_s(in.getEventObjectType());
        solr.setParent_name_t(in.getEventObjectName());
        solr.setParent_number_lcs(in.getEventObjectNumber());

        solr.setParent_ref_s(in.getEventObjectId() + "-" + in.getEventObjectType());

        solr.setOwner_lcs(in.getSubscriptionOwner());
        solr.setAdditionalProperty("related_subscription_ref_s", in.getRelatedSubscriptionId());

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

        return solr;
    }

    @Override
    public SolrAdvancedSearchDocument toContentFileIndex(AcmSubscriptionEvent in)
    {
        // No implementation needed
        return null;
    }

    @Override
    public SolrDocument toSolrQuickSearch(AcmSubscriptionEvent in)
    {

        SolrDocument solr = new SolrDocument();

        solr.setId(in.getId() + "-" + in.getObjectType());
        solr.setObject_id_s(in.getId() + "");
        solr.setObject_type_s(in.getObjectType());

        solr.setCreate_tdt(in.getCreated());
        solr.setAuthor(in.getCreator());
        solr.setLast_modified_tdt(in.getModified());
        solr.setModifier_s(in.getModifier());

        String title;
        if (in.getEventType() != null && getSubscriptionEventPlugin().getPluginProperties().containsKey(in.getEventType()))
        {
            title = (String) getSubscriptionEventPlugin().getPluginProperties().get(in.getEventType());
        } else if (in.getEventType() != null)
        {
            title = "Subscription on " + in.getEventObjectType() + ":" + in.getEventObjectId() + " - " + in.getEventObjectName();
        } else
        {
            title = "";
        }
        solr.setTitle_parseable(title);
        solr.setParent_object_id_s(Long.toString(in.getEventObjectId()));

        solr.setParent_ref_s(in.getEventObjectId() + "-" + in.getEventObjectType());

        solr.setParent_object_type_s(in.getEventObjectType());

        solr.setOwner_s(in.getSubscriptionOwner());

        return solr;
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return AcmSubscriptionEvent.class.equals(acmObjectType);
    }

    public SubscriptionEventDao getSubscriptionEventDao()
    {
        return subscriptionEventDao;
    }

    public void setSubscriptionEventDao(SubscriptionEventDao subscriptionEventDao)
    {
        this.subscriptionEventDao = subscriptionEventDao;
    }

    public AcmPlugin getSubscriptionEventPlugin()
    {
        return subscriptionEventPlugin;
    }

    public void setSubscriptionEventPlugin(AcmPlugin subscriptionEventPlugin)
    {
        this.subscriptionEventPlugin = subscriptionEventPlugin;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return AcmSubscriptionEvent.class;
    }
}
