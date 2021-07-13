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

import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.CREATOR_FULL_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.MODIFIER_FULL_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.OWNER_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_ID_S;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_NAME_T;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_NUMBER_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_REF_S;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_TYPE_S;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.TITLE_PARSEABLE;

import com.armedia.acm.audit.model.AuditEventConfig;
import com.armedia.acm.services.notification.service.NotificationFormatter;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.subscription.dao.SubscriptionEventDao;
import com.armedia.acm.services.subscription.model.AcmSubscriptionEvent;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by marjan.stefanoski on 29.01.2015.
 */
public class SubscriptionEventToSolrTransformer implements AcmObjectToSolrDocTransformer<AcmSubscriptionEvent>
{
    private final Logger LOG = LogManager.getLogger(getClass());

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

        SolrAdvancedSearchDocument solrDoc = new SolrAdvancedSearchDocument();
        LOG.debug("Creating Solr advanced search document for SUBSCRIPTION_EVENT.");

        String event = auditEventConfig.getEventTypes().getOrDefault("eventType." + in.getEventType(), "Was updated");
        String title = in.getEventObjectType() + " " + in.getEventObjectId() + ": " + event;

        mapRequiredProperties(solrDoc, in.getId(), in.getCreator(), in.getCreated(), in.getModifier(), in.getModified(),
                in.getObjectType(), title);

        solrDoc.setAdditionalProperty(TITLE_PARSEABLE, title);

        mapAdditionalProperties(in, solrDoc.getAdditionalProperties());

        return solrDoc;
    }

    @Override
    public void mapAdditionalProperties(AcmSubscriptionEvent in, Map<String, Object> additionalProperties)
    {
        if (in.getEventObjectId() != null)
        {
            additionalProperties.put(PARENT_ID_S, in.getEventObjectId());
        }
        else
        {
            additionalProperties.put(PARENT_ID_S, "");
        }

        additionalProperties.put(PARENT_TYPE_S, in.getEventObjectType());
        additionalProperties.put(PARENT_NAME_T, in.getEventObjectName());
        additionalProperties.put(PARENT_NUMBER_LCS, in.getEventObjectNumber());

        additionalProperties.put(PARENT_REF_S, in.getEventObjectId() + "-" + in.getEventObjectType());
        additionalProperties.put(OWNER_LCS, in.getSubscriptionOwner());
        additionalProperties.put("related_subscription_ref_s", in.getRelatedSubscriptionId());

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
