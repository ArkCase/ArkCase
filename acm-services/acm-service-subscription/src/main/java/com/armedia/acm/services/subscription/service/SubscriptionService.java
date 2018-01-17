package com.armedia.acm.services.subscription.service;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.services.subscription.model.AcmSubscription;

import java.sql.SQLException;
import java.util.List;

public interface SubscriptionService
{
    AcmSubscription saveSubscription(AcmSubscription subscription);

    List<AcmSubscription> getSubscriptionsByUserObjectIdAndType(String userId, Long objectId, String objectType);

    List<AcmSubscription> getSubscriptionsByUser(String userId, int start, int numRows) throws AcmObjectNotFoundException;

    int deleteSubscriptionForGivenObject(String userId, Long objectId, String objectType) throws SQLException;

    void deleteSubscriptionEventsForGivenObject(String userId, Long objectId, String objectType);
}
