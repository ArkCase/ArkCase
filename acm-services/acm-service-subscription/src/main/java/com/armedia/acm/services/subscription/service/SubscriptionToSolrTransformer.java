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

import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
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
