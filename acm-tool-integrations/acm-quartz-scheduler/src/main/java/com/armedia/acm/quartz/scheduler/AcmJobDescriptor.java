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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import java.util.Map;

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public abstract class AcmJobDescriptor implements Job
{
    private AcmJobEventPublisher jobEventPublisher;

    private static final Logger logger = LogManager.getLogger(AcmJobDescriptor.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException
    {
        logger.info("Start execution of job [{}].", this::getJobName);
        try
        {
            executeJob(context);
        }
        catch (JobExecutionException e)
        {
            logger.error("Job [{}] failed to complete. Cause: {}.", getJobName(), e.getMessage());
            jobEventPublisher.publishJobEvent(new AcmJobState(getJobName(), context.getTrigger().getKey().getName(),
                            context.getTrigger().getPreviousFireTime(), context.getNextFireTime(), false),
                    AcmJobEventPublisher.JOB_FAILED, context.getFireInstanceId());
            throw e;
        }

        logger.info("Job [{}] finished execution.", this::getJobName);
    }

    public abstract String getJobName();

    public abstract void executeJob(JobExecutionContext context) throws JobExecutionException;

    public Map<String, String> getJobData()
    {
        return null;
    }

    public AcmJobEventPublisher getJobEventPublisher()
    {
        return jobEventPublisher;
    }

    public void setJobEventPublisher(AcmJobEventPublisher jobEventPublisher)
    {
        this.jobEventPublisher = jobEventPublisher;
    }

}
