package com.armedia.acm.services.subscription.dao;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.subscription.model.AcmSubscription;
import com.armedia.acm.services.subscription.model.AcmSubscriptionEvent;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by marjan.stefanoski on 29.01.2015.
 */
public class SubscriptionDao extends AcmAbstractDao<AcmSubscription> {

    private final String AUDIT_ACTIVITY_RESULT_SUCCESS="success";

    @Override
    protected Class<AcmSubscription> getPersistenceClass() {
        return AcmSubscription.class;
    }

    public List<AcmSubscription> getSubscriptionByUserObjectIdAndType(String userId, Long objectId, String objectType)  {

        Query query = getEm().createQuery(
                "SELECT sub FROM AcmSubscription sub " +
                        "WHERE sub.userId =:userId " +
                        "AND sub.objectId =:objectId " +
                        "AND sub.subscriptionObjectType =:objectType ");

        query.setParameter("userId", userId);
        query.setParameter("objectId", objectId);
        query.setParameter("objectType", objectType);
        List<AcmSubscription> resultList = query.getResultList();

        return resultList;
    }
    public List<AcmSubscription> getListOfSubscriptionsByUser(String userId,int start, int numrows) throws AcmObjectNotFoundException {

        Query query = getEm().createQuery(
                "SELECT sub FROM AcmSubscription sub " +
                        "WHERE sub.userId =:userId " +
                        "ORDER BY sub.created");
        query.setParameter("userId", userId);
        query.setFirstResult(start);
        query.setMaxResults(numrows);

        List<AcmSubscription> results;

        results = query.getResultList();

        if( results.isEmpty()){
            throw new AcmObjectNotFoundException("SUBSCRIPTION", null, "No Subscriptions are found", null);
        }

        return results;
    }

    public List<AcmSubscriptionEvent> createListOfNewSubscriptionEventsForInserting(Date lastRunDate) throws AcmObjectNotFoundException {

        //this should be native query? Actually acm_audit_log table is mapped as JPA entity class AuditEvent
        // and I used this model.
        Query query = getEm().createQuery("SELECT aud.objectType, aud.ObjectId, " +
                "aud.userId, aud.auditDateTime, aud.fullEventType, sub.userId, " +
                "sub.objectName, sub.objectNumber " +
                "FROM AuditEvent aud, AcmSubscription sub " +
                "WHERE sub.subscriptionObjectType = aud.objectType " +
                "AND sub.objectId = aud.objectId " +
                "AND aud.eventResult =:activityResult " +
                "AND aud.eventDate >:lastRunDate ");

        query.setParameter("activityResult", AUDIT_ACTIVITY_RESULT_SUCCESS);
        query.setParameter("lastRunDate", lastRunDate);

        List<Object[]> queryResultList = query.getResultList();
        List<AcmSubscriptionEvent> result = new ArrayList<>();
        for( Object[] row : queryResultList ) {
            int i = 0;
            AcmSubscriptionEvent subscriptionEvent = new AcmSubscriptionEvent();
            subscriptionEvent.setEventObjectType((String)row[i++]);
            subscriptionEvent.setEventObjectId((Long)row[i++]);
            subscriptionEvent.setEventUser((String)row[i++]);
            subscriptionEvent.setEventDate((Date)row[i++]);
            subscriptionEvent.setEventType((String)row[i++]);
            subscriptionEvent.setSubscriptionOwner((String) row[i++]);
            subscriptionEvent.setEventObjectName((String)row[i++]);
            subscriptionEvent.setEventObjectNumber((String)row[i]);
            result.add(subscriptionEvent);
        }
        if( result.isEmpty() ) {
            throw new AcmObjectNotFoundException("SUBSCRIPTION", null, "No new Subscriptions are found", null);
        }
        return result;
    }

    @Transactional
    public int deleteSubscription( String userId, Long objectId, String objectType ) throws SQLException {
        Query deleteQuery = getEm().createQuery(
                "DELETE FROM AcmSubscription sub " +
                        "WHERE sub.userId=:userId " +
                        "AND sub.objectId=:objectId " +
                        "AND sub.subscriptionObjectType=:objectType");
        deleteQuery.setParameter("userId",userId);
        deleteQuery.setParameter("objectId",objectId);
        deleteQuery.setParameter("objectType",objectType);
        int rowCount = deleteQuery.executeUpdate();
        return rowCount;
    }
}
