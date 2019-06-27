package com.armedia.acm.calendar.service;

/*-
 * #%L
 * ACM Service: Calendar Service
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
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class PurgeCalendarJobDescriptor extends AcmJobDescriptor
{
    private AcmScheduledCalendarPurger calendarPurger;

    @Override
    public String getJobName()
    {
        return "purgeCalendarJob";
    }

    @Override
    public void executeJob(JobExecutionContext context) throws JobExecutionException
    {
        calendarPurger.executeTask();
    }

    public AcmScheduledCalendarPurger getCalendarPurger()
    {
        return calendarPurger;
    }

    public void setCalendarPurger(AcmScheduledCalendarPurger calendarPurger)
    {
        this.calendarPurger = calendarPurger;
    }
}
