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
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.listeners.TriggerListenerSupport;

public class AcmTriggerListener extends TriggerListenerSupport
{
    private AcmJobEventPublisher jobEventPublisher;

    private static final Logger logger = LogManager.getLogger(AcmTriggerListener.class);

    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context)
    {
        String jobName = context.getJobDetail().getKey().getName();
        String triggerName = trigger.getKey().getName();
        logger.debug("Trigger [{}] for job [{}] fired. Next fire time is [{}].", triggerName, jobName, trigger.getNextFireTime());
        jobEventPublisher.publishJobEvent(new AcmJobState(jobName, triggerName, trigger.getPreviousFireTime(),
                trigger.getNextFireTime(), true, false, Trigger.TriggerState.NORMAL.toString()), AcmJobEventPublisher.JOB_TRIGGERED,
                context.getFireInstanceId());
    }

    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext context, Trigger.CompletedExecutionInstruction triggerInstructionCode)
    {
        String jobName = context.getJobDetail().getKey().getName();
        String triggerName = trigger.getKey().getName();
        logger.debug("Trigger [{}] for job [{}] completed.", triggerName, jobName);
        jobEventPublisher.publishJobEvent(new AcmJobState(jobName, triggerName, trigger.getPreviousFireTime(),
                trigger.getNextFireTime(), false, false, Trigger.TriggerState.NORMAL.toString()), AcmJobEventPublisher.JOB_COMPLETED,
                context.getFireInstanceId());
    }

    @Override
    public void triggerMisfired(Trigger trigger)
    {
        logger.warn("Trigger [{}] for job misfired.", trigger.getKey());
    }

    @Override
    public String getName()
    {
        return "acmGlobalListener";
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
