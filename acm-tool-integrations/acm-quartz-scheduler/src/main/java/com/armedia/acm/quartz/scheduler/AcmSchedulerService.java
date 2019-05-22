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

import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
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
import java.util.Set;
import java.util.function.Function;
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

    public List<AcmJobDTO> getAllScheduledJobDetails()
    {
        try
        {
            Set<AcmJobDTO> currentlyExecutingJobs = new HashSet<>(getAllCurrentlyExecutingJobs());

            return scheduler.getJobKeys(GroupMatcher.anyJobGroup())
                    .stream()
                    .flatMap(it -> {
                        try
                        {
                            return scheduler.getTriggersOfJob(it).stream();
                        }
                        catch (SchedulerException e)
                        {
                            return Stream.empty();
                        }
                    })
                    .map(triggerToAcmJobDTO)
                    .map(it -> {
                        if (currentlyExecutingJobs.contains(it))
                        {
                            it.setRunning(true);
                        }
                        return it;
                    })
                    .sorted((o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(o1.getJobName(), o2.getJobName()))
                    .collect(Collectors.toList());
        }
        catch (SchedulerException e)
        {
            return new ArrayList<>();
        }
    }

    public List<AcmJobDTO> getAllCurrentlyExecutingJobs()
    {
        try
        {
            return scheduler.getCurrentlyExecutingJobs()
                    .stream()
                    .map(JobExecutionContext::getTrigger)
                    .map(triggerToAcmJobDTO)
                    .collect(Collectors.toList());
        }
        catch (SchedulerException e)
        {
            logger.warn("Failed to list all currently executing jobs. [{}]", e.getMessage());
            return new ArrayList<>();
        }
    }

    private Function<Trigger, AcmJobDTO> triggerToAcmJobDTO = it -> {
        AcmJobDTO job = new AcmJobDTO();
        String jobName = it.getJobKey().getName();
        job.setJobName(jobName);
        if (it instanceof CronTrigger)
        {
            job.setCronExpression(((CronTrigger) it).getCronExpression());
        }
        else
        {
            job.setRepeatIntervalInSeconds(((SimpleTrigger) it).getRepeatInterval());
        }
        job.setLastRun(it.getPreviousFireTime());
        job.setPaused(getTriggerState(it.getKey().getName()) == Trigger.TriggerState.PAUSED);
        return job;
    };

    public Trigger.TriggerState getTriggerState(String triggerName)
    {
        try
        {
            return scheduler.getTriggerState(TriggerKey.triggerKey(triggerName));
        }
        catch (SchedulerException e)
        {
            logger.warn("Could not read trigger [{}] state. Cause: [{}]", triggerName, e.getMessage());
        }
        return Trigger.TriggerState.NONE;
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
            logger.debug("Delete [{}] job from scheduler.", name);
            scheduler.deleteJob(JobKey.jobKey(name));
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
            logger.debug("Schedule [{}] with trigger [{}].", jobName, trigger.getKey().getName());
            scheduler.scheduleJob(jobDetail, trigger);
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
            logger.debug("Reschedule job with old trigger [{}] with trigger [{}].",
                    oldTriggerName, newTrigger.getKey().getName());
            scheduler.rescheduleJob(TriggerKey.triggerKey(oldTriggerName), newTrigger);
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
            logger.debug("Pause job [{}].", name);
            scheduler.pauseJob(JobKey.jobKey(name));
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
            logger.debug("Resume job [{}] with execution.", name);
            scheduler.resumeJob(JobKey.jobKey(name));
            publishSchedulerActionEvent(String.format("Job %s is resumed with execution.", name),
                    AcmJobEventPublisher.JOB_RESUMED, name);
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
            logger.debug("Trigger job [{}].", jobName);
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
            logger.debug("Trigger job [{}].", jobName);
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
