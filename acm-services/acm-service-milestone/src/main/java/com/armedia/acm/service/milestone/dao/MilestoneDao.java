package com.armedia.acm.service.milestone.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.service.milestone.model.AcmMilestone;

/**
 * Created by armdev on 12/5/14.
 */
public class MilestoneDao extends AcmAbstractDao<AcmMilestone>
{
    @Override
    protected Class<AcmMilestone> getPersistenceClass()
    {
        return AcmMilestone.class;
    }
}
