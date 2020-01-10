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

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.subscription.model.AcmSubscription;
import com.armedia.acm.services.subscription.model.AcmSubscriptionEvent;
import com.armedia.acm.services.subscription.model.SubscriptionConstants;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by marjan.stefanoski on 29.01.2015.
 */
public class SubscriptionDao extends AcmAbstractDao<AcmSubscription>
{
    private Logger LOG = LogManager.getLogger(getClass());

    @Override
    protected Class<AcmSubscription> getPersistenceClass()
    {
        return AcmSubscription.class;
    }

    public List<AcmSubscription> getSubscriptionByUserObjectIdAndType(String userId, Long objectId, String objectType)
    {

        TypedQuery<AcmSubscription> query = getEm().createQuery("SELECT sub FROM AcmSubscription sub " + "WHERE sub.userId =:userId "
                + "AND sub.objectId =:objectId " + "AND sub.subscriptionObjectType =:objectType ", AcmSubscription.class);

        query.setParameter("userId", userId);
        query.setParameter("objectId", objectId);
        query.setParameter("objectType", objectType);
        List<AcmSubscription> resultList = query.getResultList();

        return resultList;
    }

    public List<AcmSubscription> getListOfSubscriptionsByUser(String userId, int start, int numrows) throws AcmObjectNotFoundException
    {

        Query query = getEm().createQuery("SELECT sub FROM AcmSubscription sub " + "WHERE sub.userId =:userId " + "ORDER BY sub.created");
        query.setParameter("userId", userId);
        if (numrows > -1)
        {
            query.setFirstResult(start);
            query.setMaxResults(numrows);
        }

        List<AcmSubscription> results;

        results = query.getResultList();

        if (results.isEmpty())
        {
            throw new AcmObjectNotFoundException("SUBSCRIPTION", null, "No Subscriptions are found", null);
        }

        return results;
    }

    public List<AcmSubscriptionEvent> createListOfNewSubscriptionEventsForInserting(Date lastRunDate, List<String> eventsToBeRemoved)
            throws AcmObjectNotFoundException
    {

        // this should be native query? Actually acm_audit_log table is mapped as JPA entity class AuditEvent
        // and I used this model.
        Query query;
        if (eventsToBeRemoved != null)
        {
            query = getEm().createQuery("SELECT aud.objectType, aud.objectId, "
                    + "aud.userId, aud.eventDate, aud.fullEventType, sub.userId, " + "sub.objectName, sub.objectTitle, sub.subscriptionId "
                    + "FROM AuditEvent aud, AcmSubscription sub " + "WHERE sub.subscriptionObjectType = aud.objectType "
                    + "AND sub.objectId = aud.objectId " + "AND aud.eventResult =:activityResult " + "AND aud.eventDate >:lastRunDate "
                    + "AND aud.fullEventType NOT IN :eventsToBeRemoved ");
        }
        else
        {
            query = getEm().createQuery("SELECT aud.objectType, aud.objectId, "
                    + "aud.userId, aud.eventDate, aud.fullEventType, sub.userId, " + "sub.objectName, sub.objectTitle, sub.subscriptionId "
                    + "FROM AuditEvent aud, AcmSubscription sub " + "WHERE sub.subscriptionObjectType = aud.objectType "
                    + "AND sub.objectId = aud.objectId " + "AND aud.eventResult =:activityResult " + "AND aud.eventDate >:lastRunDate");
        }

        query.setParameter("activityResult", SubscriptionConstants.AUDIT_ACTIVITY_RESULT_SUCCESS);
        query.setParameter("lastRunDate", lastRunDate);
        if (eventsToBeRemoved != null)
        {
            query.setParameter("eventsToBeRemoved", eventsToBeRemoved);
        }

        List<Object[]> queryResultList = query.getResultList();
        List<AcmSubscriptionEvent> result = new ArrayList<>();
        for (Object[] row : queryResultList)
        {
            int i = 0;
            AcmSubscriptionEvent subscriptionEvent = new AcmSubscriptionEvent();
            subscriptionEvent.setEventObjectType((String) row[i++]);
            subscriptionEvent.setEventObjectId((Long) row[i++]);
            subscriptionEvent.setEventUser((String) row[i++]);
            subscriptionEvent.setEventDate((Date) row[i++]);
            subscriptionEvent.setEventType((String) row[i++]);
            subscriptionEvent.setSubscriptionOwner((String) row[i++]);
            subscriptionEvent.setEventObjectName((String) row[i++]);
            subscriptionEvent.setEventObjectNumber((String) row[i++]);
            subscriptionEvent.setRelatedSubscriptionId(String.format("%d-%s", (Long) row[i++], SubscriptionConstants.OBJECT_TYPE));
            result.add(subscriptionEvent);
        }
        if (result.isEmpty())
        {
            LOG.info("No new Subscriptions found");

        }
        return result;
    }

    @Transactional
    public int deleteSubscription(String userId, Long objectId, String objectType) throws SQLException
    {

        Query selectQuery = getEm().createQuery("SELECT sub FROM AcmSubscription sub " + "WHERE sub.userId=:userId "
                + "AND sub.objectId=:objectId " + "AND sub.subscriptionObjectType=:objectType");
        selectQuery.setParameter("userId", userId);
        selectQuery.setParameter("objectId", objectId);
        selectQuery.setParameter("objectType", objectType);

        List<AcmSubscription> results;

        results = selectQuery.getResultList();

        AcmSubscription subscriptionForDel;
        int rowCount = 0;
        if (!results.isEmpty())
        {
            subscriptionForDel = results.get(0);
            getEm().remove(subscriptionForDel);
            rowCount = 1;
        }
        return rowCount;
    }

}
