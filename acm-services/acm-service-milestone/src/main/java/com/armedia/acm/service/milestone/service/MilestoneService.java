package com.armedia.acm.service.milestone.service;

/*-
 * #%L
 * ACM Service: Milestones
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.service.milestone.dao.MilestoneDao;
import com.armedia.acm.service.milestone.model.AcmMilestone;

import java.time.LocalDate;

/**
 * Created by armdev on 12/5/14.
 */
public class MilestoneService
{
    private MilestoneDao dao;

    public void saveMilestone(Long objectId, String objectType, String milestoneName)
    {
        AcmMilestone milestone = new AcmMilestone();
        milestone.setMilestoneDate(LocalDate.now());
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
