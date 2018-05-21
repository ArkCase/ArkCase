package com.armedia.acm.services.subscription.dao;

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

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.subscription.model.AcmSubscriptionEvent;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import java.util.List;

/**
 * Created by marjan.stefanoski on 29.01.2015.
 */
public class SubscriptionEventDao extends AcmAbstractDao<AcmSubscriptionEvent>
{

    @Override
    protected Class<AcmSubscriptionEvent> getPersistenceClass()
    {
        return AcmSubscriptionEvent.class;
    }

    public List<AcmSubscriptionEvent> deleteSubscriptionEvents(String userId, Long objectId, String objectType)
    {
        // get all subscription events before deleting to publish AdmDatabaseChangesEvent later
        TypedQuery<AcmSubscriptionEvent> query = getEm()
                .createQuery("select event from AcmSubscriptionEvent event where event.eventObjectType=:objectType " +
                        "and event.eventObjectId=:objectId and event.subscriptionOwner=:userId", AcmSubscriptionEvent.class);

        query.setParameter("objectType", objectType);
        query.setParameter("objectId", objectId);
        query.setParameter("userId", userId);

        List<AcmSubscriptionEvent> subscriptionEvents = query.getResultList();

        Query deleteQuery = getEm().createQuery("delete from AcmSubscriptionEvent event where event.eventObjectType=:objectType " +
                "and event.eventObjectId=:objectId and event.subscriptionOwner=:userId");

        deleteQuery.setParameter("objectType", objectType);
        deleteQuery.setParameter("objectId", objectId);
        deleteQuery.setParameter("userId", userId);
        deleteQuery.executeUpdate();

        return subscriptionEvents;
    }
}
