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

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.utils.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AcmSchedulerService
{
    private static final Logger logger = LoggerFactory.getLogger(AcmSchedulerService.class);
    private Scheduler scheduler;
    private AcmJobEventPublisher jobEventPublisher;

    public List<String> getAllScheduledJobs()
    {
        try
        {
            return scheduler.getJobKeys(GroupMatcher.anyJobGroup())
                    .stream()
                    .map(Key::getName)
                    .collect(Collectors.toList());
        }
        catch (SchedulerException e)
        {
            logger.warn("Failed to list all scheduled jobs. [{}]", e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<JobDetail> getAllCurrentlyExecutingJobs()
    {
        try
        {
            return scheduler.getCurrentlyExecutingJobs()
                    .stream()
                    .map(JobExecutionContext::getJobDetail)
                    .collect(Collectors.toList());
        }
        catch (SchedulerException e)
        {
            logger.warn("Failed to list all currently executing jobs. [{}]", e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean isJobScheduled(String triggerName)
    {
        try
        {
            return scheduler.checkExists(TriggerKey.triggerKey(triggerName));
        }
        catch (SchedulerException e)
        {
            logger.warn("Failed to check if there is a scheduled trigger [{}].", triggerName);
            return false;
        }
    }

    public void deleteJob(String name)
    {
        try
        {
            scheduler.deleteJob(JobKey.jobKey(name));
            logger.debug("Deleted [{}] job from scheduler.", name);
            publishSchedulerActionEvent(String.format("Job %s is deleted from the scheduler.", name),
                    AcmJobEventPublisher.JOB_DELETED, name);
        }
        catch (SchedulerException e)
        {
            logger.warn("Failed to delete job [{}] from scheduler. [{}]", name, e.getMessage());
        }
    }

    public void scheduleJob(JobDetail jobDetail, Trigger trigger)
    {
        String jobName = jobDetail.getKey().getName();
        try
        {
            scheduler.scheduleJob(jobDetail, trigger);
            logger.debug("Scheduled [{}] with trigger [{}].", jobName, trigger.getKey().getName());
            publishSchedulerActionEvent(String.format("Job %s is scheduled.", jobDetail.getKey().getName()),
                    AcmJobEventPublisher.JOB_SCHEDULED, jobName);
        }
        catch (SchedulerException e)
        {
            logger.warn("Failed to schedule job of class [{}]. [{}]", jobDetail.getJobClass(), e.getMessage());
        }
    }

    public void rescheduleJob(String oldTriggerName, Trigger newTrigger)
    {
        try
        {
            scheduler.rescheduleJob(TriggerKey.triggerKey(oldTriggerName), newTrigger);
            logger.debug("Rescheduled job with old trigger [{}] with trigger [{}].",
                    oldTriggerName, newTrigger.getKey().getName());
            publishSchedulerActionEvent(String.format("Rescheduled job with old trigger %s with trigger %s", oldTriggerName,
                    newTrigger.getKey().getName()), AcmJobEventPublisher.JOB_RESCHEDULED, oldTriggerName);
        }
        catch (SchedulerException e)
        {
            logger.warn("Failed to reschedule job with old trigger name [{}]. [{}]", oldTriggerName, e.getMessage());
        }
    }

    public void rescheduleJob(JobDetail jobDetail, Trigger... triggers)
    {
        String jobName = jobDetail.getKey().getName();
        try
        {
            scheduler.scheduleJob(jobDetail, new HashSet<>(Arrays.asList(triggers)), true);
            String message = String.format("Rescheduled job [%s] with new triggers [%s].", jobName,
                    Arrays.stream(triggers)
                            .map(it -> it.getKey().getName())
                            .collect(Collectors.joining(",")));
            logger.debug(message);
            publishSchedulerActionEvent(message, AcmJobEventPublisher.JOB_RESCHEDULED, jobName);
        }
        catch (SchedulerException e)
        {
            logger.warn("Failed to reschedule job [{}]. [{}]", jobDetail.getKey().getName(), e.getMessage());
        }
    }

    public void pauseJob(String name)
    {
        try
        {
            scheduler.pauseJob(JobKey.jobKey(name));
            logger.debug("Paused job [{}].", name);
            publishSchedulerActionEvent(String.format("Job %s is paused.", name), AcmJobEventPublisher.JOB_PAUSED, name);
        }
        catch (SchedulerException e)
        {
            logger.warn("Failed to pause job [{}]. [{}]", name, e.getMessage());
        }
    }

    public void resumeJob(String name)
    {
        try
        {
            publishSchedulerActionEvent(String.format("Job %s is resumed with execution.", name),
                    AcmJobEventPublisher.JOB_RESUMED, name);
            scheduler.resumeJob(JobKey.jobKey(name));
            logger.debug("Resumed job [{}] with execution.", name);
        }
        catch (SchedulerException e)
        {
            logger.warn("Failed to resume paused job [{}]. [{}]", name, e.getMessage());
        }
    }

    public JobDataMap getJobDataMap(String name)
    {
        try
        {
            return scheduler.getJobDetail(JobKey.jobKey(name)).getJobDataMap();
        }
        catch (SchedulerException e)
        {
            logger.warn("Failed to retrieve job data map for job [{}]. [{}]", name, e.getMessage());
            return null;
        }
    }

    public Date getTriggerPreviousFireTime(String triggerName)
    {
        try
        {
            return scheduler.getTrigger(TriggerKey.triggerKey(triggerName)).getPreviousFireTime();
        }
        catch (SchedulerException e)
        {
            logger.warn("Trigger with name [{}] was not found. [{}]", triggerName, e.getMessage());
            return null;
        }
    }

    public void triggerJob(String jobName)
    {
        try
        {
            scheduler.triggerJob(JobKey.jobKey(jobName));
        }
        catch (SchedulerException e)
        {
            logger.warn("Failed to execute job [{}] on demand. [{}]", jobName, e.getMessage());
        }
    }

    public void triggerJob(String jobName, JobDataMap jobDataMap)
    {
        try
        {
            scheduler.triggerJob(JobKey.jobKey(jobName), jobDataMap);
        }
        catch (SchedulerException e)
        {
            logger.warn("Failed to execute job [{}] on demand with data map [{}]. [{}]", jobName, jobDataMap, e.getMessage());
        }
    }

    private void publishSchedulerActionEvent(String message, String action, String jobName)
    {
        try
        {
            jobEventPublisher.publishJobEvent(message, action, scheduler.getSchedulerInstanceId(), jobName);
        }
        catch (SchedulerException e)
        {
            logger.warn("Failed to retrieve Quartz scheduler instance id.");
        }
    }

    public Scheduler getScheduler()
    {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler)
    {
        this.scheduler = scheduler;
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
