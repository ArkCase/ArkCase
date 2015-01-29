package com.armedia.acm.services.subscription.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.subscription.model.AcmSubscription;

/**
 * Created by marjan.stefanoski on 29.01.2015.
 */
public class SubscriptionDao extends AcmAbstractDao<AcmSubscription> {

    @Override
    protected Class<AcmSubscription> getPersistenceClass() {
        return AcmSubscription.class;
    }
}
