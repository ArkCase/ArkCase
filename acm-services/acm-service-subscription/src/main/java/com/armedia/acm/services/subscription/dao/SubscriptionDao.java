package com.armedia.acm.services.subscription.dao;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.subscription.model.AcmSubscription;
import com.armedia.acm.services.subscription.model.AcmSubscriptionEvent;
import com.armedia.acm.services.subscription.model.SubscriptionConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private Logger LOG = LoggerFactory.getLogger(getClass());

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
        if (numrows != -1)
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
        Query query = getEm().createQuery("SELECT aud.objectType, aud.objectId, "
                + "aud.userId, aud.eventDate, aud.fullEventType, sub.userId, " + "sub.objectName, sub.objectTitle, sub.subscriptionId "
                + "FROM AuditEvent aud, AcmSubscription sub " + "WHERE sub.subscriptionObjectType = aud.objectType "
                + "AND sub.objectId = aud.objectId " + "AND aud.eventResult =:activityResult " + "AND aud.eventDate >:lastRunDate "
                + (eventsToBeRemoved != null ? "AND aud.fullEventType NOT IN :eventsToBeRemoved " : ""));

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
            subscriptionEvent.setRelatedSubscriptionId(String.format("%d-%s", row[i++], SubscriptionConstants.OBJECT_TYPE));
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
