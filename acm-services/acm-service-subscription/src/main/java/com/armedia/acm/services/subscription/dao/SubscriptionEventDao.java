package com.armedia.acm.services.subscription.dao;

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
