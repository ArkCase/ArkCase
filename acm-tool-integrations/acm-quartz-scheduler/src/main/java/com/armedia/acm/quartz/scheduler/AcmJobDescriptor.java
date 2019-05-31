package com.armedia.acm.quartz.scheduler;

/*-
 * #%L
 * ACM Tool Integrations: Quartz Scheduler
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

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public abstract class AcmJobDescriptor implements Job
{
    private AcmJobEventPublisher jobEventPublisher;

    private static final Logger logger = LoggerFactory.getLogger(AcmJobDescriptor.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException
    {
        String startInfo = String.format("Start execution of job %s.", getJobName());
        logger.debug(startInfo);
        jobEventPublisher.publishJobEvent(startInfo, AcmJobEventPublisher.JOB_TRIGGERED, context.getFireInstanceId(), getJobName());
        try
        {
            executeJob(context);
        }
        catch (JobExecutionException e)
        {
            String completeFailedInfo = String.format("Job %s failed to complete. Cause: %s", getJobName(), e.getMessage());
            jobEventPublisher.publishJobEvent(completeFailedInfo,
                    AcmJobEventPublisher.JOB_FAILED, context.getFireInstanceId(), getJobName());
            throw e;
        }

        String completeInfo = String.format("Job %s finished execution.", getJobName());
        jobEventPublisher.publishJobEvent(completeInfo,
                AcmJobEventPublisher.JOB_COMPLETED, context.getFireInstanceId(), getJobName());
    }

    public abstract String getJobName();

    public abstract void executeJob(JobExecutionContext context) throws JobExecutionException;

    public AcmJobEventPublisher getJobEventPublisher()
    {
        return jobEventPublisher;
    }

    public void setJobEventPublisher(AcmJobEventPublisher jobEventPublisher)
    {
        this.jobEventPublisher = jobEventPublisher;
    }

}
