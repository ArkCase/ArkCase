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

import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.OWNER_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_ID_S;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_TYPE_S;

import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.subscription.dao.SubscriptionDao;
import com.armedia.acm.services.subscription.model.AcmSubscription;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by marjan.stefanoski on 11.03.2015.
 */
public class SubscriptionToSolrTransformer implements AcmObjectToSolrDocTransformer<AcmSubscription>
{
    private final Logger LOG = LogManager.getLogger(getClass());

    private SubscriptionDao subscriptionDao;

    @Override
    public List<AcmSubscription> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getSubscriptionDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(AcmSubscription in)
    {

        SolrAdvancedSearchDocument solrDoc = new SolrAdvancedSearchDocument();
        LOG.debug("Creating Solr advanced search document for SUBSCRIPTION.");

        mapRequiredProperties(solrDoc, in.getId(), in.getCreator(), in.getCreated(), in.getModifier(), in.getModified(), in.getObjectType(),
                null);

        mapAdditionalProperties(in, solrDoc.getAdditionalProperties());

        return solrDoc;
    }

    @Override
    public void mapAdditionalProperties(AcmSubscription in, Map<String, Object> additionalProperties)
    {
        additionalProperties.put(PARENT_ID_S, Long.toString(in.getObjectId()));
        additionalProperties.put(PARENT_TYPE_S, in.getSubscriptionObjectType());
        additionalProperties.put(OWNER_LCS, in.getUserId());
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
