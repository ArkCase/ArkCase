package com.armedia.acm.services.search.service;

/*-
 * #%L
 * ACM Service: Search
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import com.armedia.acm.quartz.scheduler.AcmJobDescriptor;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class AcmJpaBatchUpdateJobDescriptor extends AcmJobDescriptor
{
    private AcmJpaBatchUpdateService jpaBatchUpdateService;

    @Override
    public String getJobName()
    {
        return "jpaBatchUpdateJob";
    }

    @Override
    public void executeJob(JobExecutionContext context) throws JobExecutionException
    {
        JobDataMap triggerDataMap = context.getTrigger().getJobDataMap();
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();

        try
        {
            if (triggerDataMap != null && triggerDataMap.size() != 0)
            {
                jpaBatchUpdateService.jpaBatchUpdate(triggerDataMap);
                jobDataMap.clear();
                jobDataMap.putAll(triggerDataMap);
            }
            else
            {
                jpaBatchUpdateService.jpaBatchUpdate(jobDataMap);
            }
        }
        catch (InterruptedException e)
        {
            throw new JobExecutionException(e);
        }
    }

    public AcmJpaBatchUpdateService getJpaBatchUpdateService()
    {
        return jpaBatchUpdateService;
    }

    public void setJpaBatchUpdateService(AcmJpaBatchUpdateService jpaBatchUpdateService)
    {
        this.jpaBatchUpdateService = jpaBatchUpdateService;
    }
}
