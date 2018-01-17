package com.armedia.acm.services.subscription.service.impl;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.data.AcmObjectChangelist;
import com.armedia.acm.services.subscription.dao.SubscriptionDao;
import com.armedia.acm.services.subscription.dao.SubscriptionEventDao;
import com.armedia.acm.services.subscription.model.AcmSubscription;
import com.armedia.acm.services.subscription.model.AcmSubscriptionEvent;
import com.armedia.acm.services.subscription.model.SubscriptionConstants;
import com.armedia.acm.services.subscription.service.SubscriptionEventPublisher;
import com.armedia.acm.services.subscription.service.SubscriptionService;

import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

public class SubscriptionServiceImpl implements SubscriptionService
{

    private SubscriptionDao subscriptionDao;
    private SubscriptionEventDao subscriptionEventDao;
    private SubscriptionEventPublisher subscriptionEventPublisher;

    @Override
    public AcmSubscription saveSubscription(AcmSubscription subscription)
    {
        return getSubscriptionDao().save(subscription);
    }

    @Override
    public List<AcmSubscription> getSubscriptionsByUserObjectIdAndType(String userId, Long objectId, String objectType)
    {
        return getSubscriptionDao().getSubscriptionByUserObjectIdAndType(userId, objectId, objectType);
    }

    @Override
    public List<AcmSubscription> getSubscriptionsByUser(String userId, int start, int numRows) throws AcmObjectNotFoundException
    {
        return getSubscriptionDao().getListOfSubscriptionsByUser(userId, start, numRows);
    }

    @Override
    public int deleteSubscriptionForGivenObject(String userId, Long objectId, String objectType) throws SQLException
    {

        int rowsEffected = getSubscriptionDao().deleteSubscription(userId, objectId, objectType);
        if (rowsEffected == SubscriptionConstants.NO_ROW_DELETED)
        {
            getSubscriptionEventPublisher().publishSubscriptionDeletedEvent(userId, objectId, objectType, false);
        }
        else
        {
            getSubscriptionEventPublisher().publishSubscriptionDeletedEvent(userId, objectId, objectType, true);
        }
        return rowsEffected;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Async("deleteTaskExecutor")
    @Override
    public void deleteSubscriptionEventsForGivenObject(String userId, Long objectId, String objectType)
    {
        List<AcmSubscriptionEvent> deletedObjects = getSubscriptionEventDao().deleteSubscriptionEvents(userId, objectId, objectType);

        AcmObjectChangelist changelist = new AcmObjectChangelist();
        changelist.getDeletedObjects().addAll(deletedObjects);

        getSubscriptionEventPublisher().publishDeletedSubscriptionEvents(changelist);
    }

    public SubscriptionEventDao getSubscriptionEventDao()
    {
        return subscriptionEventDao;
    }

    public void setSubscriptionEventDao(SubscriptionEventDao subscriptionEventDao)
    {
        this.subscriptionEventDao = subscriptionEventDao;
    }

    public SubscriptionEventPublisher getSubscriptionEventPublisher()
    {
        return subscriptionEventPublisher;
    }

    public void setSubscriptionEventPublisher(SubscriptionEventPublisher subscriptionEventPublisher)
    {
        this.subscriptionEventPublisher = subscriptionEventPublisher;
    }

    public SubscriptionDao getSubscriptionDao()
    {
        return subscriptionDao;
    }

    public void setSubscriptionDao(SubscriptionDao subscriptionDao)
    {
        this.subscriptionDao = subscriptionDao;
    }
}
