package com.armedia.acm.services.subscription.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.subscription.model.AcmSubscriptionEvent;

/**
 * Created by marjan.stefanoski on 29.01.2015.
 */
public class SubscriptionEventDao extends AcmAbstractDao<AcmSubscriptionEvent> {

    @Override
    protected Class<AcmSubscriptionEvent> getPersistenceClass() {
        return AcmSubscriptionEvent.class;
    }
}
