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
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.utils.Key;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AcmSchedulerService
{
    private static final Logger logger = LogManager.getLogger(AcmSchedulerService.class);
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

    public Map<String, AcmJobState> getAllScheduledJobDetails()
    {
        try
        {
            return scheduler.getJobKeys(GroupMatcher.jobGroupEquals("DEFAULT"))
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
                    .map(triggerToJobState)
                    .collect(Collectors.toMap(AcmJobState::getTriggerName, Function.identity(),
                            (k, v) -> {
                                throw new RuntimeException(String.format("Duplicate key found for values %s, %s",
                                        k, v.getJobName()));
                            },
                            TreeMap::new));
        }
        catch (SchedulerException e)
        {
            logger.warn("Failed to list all scheduled jobs. [{}]", e.getMessage());
            return new HashMap<>();
        }
    }

    private Function<Trigger, AcmJobState> triggerToJobState = it -> {
        String jobName = it.getJobKey().getName();
        String triggerName = it.getKey().getName();
        AcmJobState job = new AcmJobState(jobName, triggerName);
        if (it instanceof CronTrigger)
        {
            job.setCronExpression(((CronTrigger) it).getCronExpression());
        }
        else
        {
            long intervalInMilliseconds = ((SimpleTrigger) it).getRepeatInterval();
            job.setRepeatIntervalInSeconds(TimeUnit.MILLISECONDS.toSeconds(intervalInMilliseconds));
        }
        job.setLastRun(it.getPreviousFireTime());
        job.setNextRun(it.getNextFireTime());
        job.setPaused(getTriggerState(triggerName) == Trigger.TriggerState.PAUSED);
        job.setRunning(getTriggerState(triggerName) == Trigger.TriggerState.BLOCKED);
        job.setTriggerState(getTriggerState(triggerName).toString());
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

    @Transactional
    public void deleteJob(String name)
    {
        try
        {
            logger.info("Delete [{}] job from scheduler.", name);
            scheduler.deleteJob(JobKey.jobKey(name));
            publishSchedulerActionEvent(AcmJobEventPublisher.JOB_DELETED, new AcmJobState(name, null));
        }
        catch (SchedulerException e)
        {
            logger.warn("Failed to delete job [{}] from scheduler. [{}]", name, e.getMessage());
        }
    }

    @Transactional
    public void scheduleJob(JobDetail jobDetail, Trigger trigger)
    {
        String jobName = jobDetail.getKey().getName();
        String triggerName = trigger.getKey().getName();
        try
        {
            logger.info("Schedule [{}] with trigger [{}].", jobName, triggerName);
            scheduler.scheduleJob(jobDetail, trigger);
            publishSchedulerActionEvent(AcmJobEventPublisher.JOB_SCHEDULED, new AcmJobState(jobName, triggerName));
        }
        catch (SchedulerException e)
        {
            logger.warn("Failed to schedule job of class [{}]. [{}]", jobDetail.getJobClass(), e.getMessage());
        }
    }

    @Transactional
    public void rescheduleJob(String oldTriggerName, Trigger newTrigger)
    {
        try
        {
            String newTriggerName = newTrigger.getKey().getName();
            logger.debug("Reschedule job with old trigger [{}] with trigger [{}].",
                    oldTriggerName, newTriggerName);
            scheduler.rescheduleJob(TriggerKey.triggerKey(oldTriggerName), newTrigger);
            publishSchedulerActionEvent(AcmJobEventPublisher.JOB_RESCHEDULED,
                    new AcmJobState(newTrigger.getJobKey().getName(), newTriggerName));
        }
        catch (SchedulerException e)
        {
            logger.warn("Failed to reschedule job with old trigger name [{}]. [{}]", oldTriggerName, e.getMessage());
        }
    }

    @Transactional
    public void pauseJob(String name)
    {
        try
        {
            logger.info("Pause job [{}].", name);
            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(JobKey.jobKey(name));
            scheduler.pauseJob(JobKey.jobKey(name));
            triggers.forEach(it -> publishSchedulerActionEvent(AcmJobEventPublisher.JOB_PAUSED,
                    new AcmJobState(name, it.getKey().getName(), it.getPreviousFireTime(), it.getNextFireTime(), false, true)));
        }
        catch (SchedulerException e)
        {
            logger.warn("Failed to pause job [{}]. [{}]", name, e.getMessage());
        }
    }

    @Transactional
    public void resumeJob(String name)
    {
        try
        {
            logger.info("Resume job [{}] with execution.", name);
            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(JobKey.jobKey(name));
            scheduler.resumeJob(JobKey.jobKey(name));
            triggers.forEach(it -> publishSchedulerActionEvent(AcmJobEventPublisher.JOB_RESUMED,
                    new AcmJobState(name, it.getKey().getName(), it.getPreviousFireTime(), it.getNextFireTime(), false)));
        }
        catch (SchedulerException e)
        {
            logger.warn("Failed to resume paused job [{}]. [{}]", name, e.getMessage());
        }
    }

    @Transactional
    public void restoreTriggerState(String triggerName)
    {
        try
        {
            Trigger trigger = scheduler.getTrigger(TriggerKey.triggerKey(triggerName));
            Trigger.TriggerState state = getTriggerState(triggerName);
            if (state == Trigger.TriggerState.BLOCKED || state == Trigger.TriggerState.ERROR)
            {
                logger.info("Restore trigger [{}] with execution.", triggerName);
                scheduler.resetTriggerFromErrorState(trigger.getKey());
                publishSchedulerActionEvent(AcmJobEventPublisher.JOB_RESUMED,
                        new AcmJobState(triggerName, trigger.getKey().getName(),
                                trigger.getPreviousFireTime(), trigger.getNextFireTime(), false));
            }
        }
        catch (SchedulerException e)
        {
            logger.warn("Failed to restore trigger [{}]. [{}]", triggerName, e.getMessage());
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

    /**
     * We need to reuse existing Trigger instead of creating new one.
     * This way we can update the prev_fire_time and next_fire_time in QRTZ_TRIGGERS table.
     *
     * @param jobName
     *            existing job name from QRTZ_JOB_DETAILS
     */
    @Transactional
    public void triggerJob(String jobName)
    {
        try
        {
            logger.info("Trigger job [{}].", jobName);

            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(JobKey.jobKey(jobName));
            for (Trigger it : triggers)
            {
                Trigger newTrigger = it.getTriggerBuilder().startAt(new Date()).build();
                scheduler.rescheduleJob(it.getKey(), newTrigger);

                Trigger.TriggerState state = getTriggerState(newTrigger.getKey().getName());

                publishSchedulerActionEvent(AcmJobEventPublisher.JOB_TRIGGERED,
                        new AcmJobState(jobName, newTrigger.getKey().getName(),
                                newTrigger.getPreviousFireTime(), newTrigger.getNextFireTime(), true, false, state.toString()));
            }
        }
        catch (SchedulerException e)
        {
            logger.warn("Failed to execute job [{}] on demand. [{}]", jobName, e.getMessage());
        }
    }

    @Transactional
    public void triggerJob(String jobName, JobDataMap jobDataMap)
    {
        try
        {
            logger.info("Trigger job [{}].", jobName);
            scheduler.triggerJob(JobKey.jobKey(jobName), jobDataMap);
        }
        catch (SchedulerException e)
        {
            logger.warn("Failed to execute job [{}] on demand with data map [{}]. [{}]", jobName, jobDataMap, e.getMessage());
        }
    }

    private void publishSchedulerActionEvent(String action, AcmJobState jobState)
    {
        try
        {
            jobEventPublisher.publishJobEvent(jobState, action, scheduler.getSchedulerInstanceId());
        }
        catch (SchedulerException e)
        {
            logger.warn("Failed to retrieve Quartz scheduler instance id. Cause: {}", e.getMessage());
        }
    }

    public Scheduler getScheduler()
    {
        return scheduler;
    }

    public Trigger getTrigger(String syncJobName)
    {
        try
        {
            return this.scheduler.getTrigger(TriggerKey.triggerKey(syncJobName + "Trigger"));
        }
        catch (SchedulerException e)
        {
            logger.warn("Failed to retrieve Trigger information. Cause: {}", e.getMessage());
            return null;
        }
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
