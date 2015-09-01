package com.armedia.acm.plugins.casefile.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.casefile.model.AcmQueue;

/**
 * Created by nebojsha on 31.08.2015.
 */
public class AcmQueueDao extends AcmAbstractDao<AcmQueue>
{

    @Override
    protected Class<AcmQueue> getPersistenceClass()
    {
        return AcmQueue.class;
    }
}
