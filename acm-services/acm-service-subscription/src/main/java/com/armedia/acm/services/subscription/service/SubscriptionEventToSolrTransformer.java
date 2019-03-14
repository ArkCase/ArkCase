package com.armedia.acm.services.subscription.service;

/*-
 * #%L
 * ACM Service: Subscription
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

import com.armedia.acm.audit.model.AuditEventConfig;
import com.armedia.acm.services.notification.service.NotificationFormatter;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.subscription.dao.SubscriptionEventDao;
import com.armedia.acm.services.subscription.model.AcmSubscriptionEvent;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import java.util.Date;
import java.util.List;

/**
 * Created by marjan.stefanoski on 29.01.2015.
 */
public class SubscriptionEventToSolrTransformer implements AcmObjectToSolrDocTransformer<AcmSubscriptionEvent>
{

    private SubscriptionEventDao subscriptionEventDao;
    private UserDao userDao;
    private NotificationFormatter notificationFormatter;
    private AuditEventConfig auditEventConfig;

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

        String event = auditEventConfig.getEventTypes().getOrDefault("eventType." + in.getEventType(), "Was updated");
        String title = in.getEventObjectType() + " " + in.getEventObjectId() + ": " + event;

        solr.setTitle_parseable(notificationFormatter.replaceSubscriptionTitle(title, in.getEventObjectType(), in.getEventObjectType()));

        if (in.getEventObjectId() != null)
        {
            solr.setParent_id_s(Long.toString(in.getEventObjectId()));
        }
        else
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

        String title = "Subscription on " + in.getEventObjectType() + ":" + in.getEventObjectId() + " - " + in.getEventObjectName();
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

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public NotificationFormatter getNotificationFormatter()
    {
        return notificationFormatter;
    }

    public void setNotificationFormatter(NotificationFormatter notificationFormatter)
    {
        this.notificationFormatter = notificationFormatter;
    }

    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return AcmSubscriptionEvent.class;
    }

    public AuditEventConfig getAuditEventConfig()
    {
        return auditEventConfig;
    }

    public void setAuditEventConfig(AuditEventConfig auditEventConfig)
    {
        this.auditEventConfig = auditEventConfig;
    }
}
