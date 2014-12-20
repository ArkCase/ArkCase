package com.armedia.acm.service.milestone.service;

import com.armedia.acm.service.milestone.dao.MilestoneDao;
import com.armedia.acm.service.milestone.model.AcmMilestone;

import java.util.Date;

/**
 * Created by armdev on 12/5/14.
 */
public class MilestoneService
{
    private MilestoneDao dao;

    public void saveMilestone(Long objectId, String objectType, String milestoneName)
    {
        Date achieved = new Date();

        AcmMilestone milestone = new AcmMilestone();
        milestone.setMilestoneDate(achieved);
        milestone.setMilestoneName(milestoneName);
        milestone.setObjectId(objectId);
        milestone.setObjectType(objectType);

        getDao().save(milestone);

    }

    public MilestoneDao getDao()
    {
        return dao;
    }

    public void setDao(MilestoneDao dao)
    {
        this.dao = dao;
    }
}
