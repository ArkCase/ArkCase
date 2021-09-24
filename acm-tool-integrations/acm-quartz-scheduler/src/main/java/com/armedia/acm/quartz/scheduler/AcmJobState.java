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

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AcmJobState
{
    private String jobName;

    private String triggerName;

    private Long repeatIntervalInSeconds;

    private String cronExpression;

    private Date lastRun;

    private Date nextRun;

    private boolean isRunning;

    private boolean isPaused;

    private String triggerState;

    public AcmJobState(String jobName, String triggerName)
    {
        this.jobName = jobName;
        this.triggerName = triggerName;
        this.isRunning = false;
        this.isPaused = false;
    }

    public AcmJobState(String jobName, String triggerName, Date lastRun, Date nextRun, boolean isRunning)
    {
        this(jobName, triggerName, lastRun, nextRun, isRunning, false);
    }

    public AcmJobState(String jobName, String triggerName, Date lastRun, Date nextRun, boolean isRunning,
            boolean isPaused)
    {
        this.jobName = jobName;
        this.triggerName = triggerName;
        this.lastRun = lastRun;
        this.nextRun = nextRun;
        this.isRunning = isRunning;
        this.isPaused = isPaused;
    }

    public AcmJobState(String jobName, String triggerName, Date lastRun, Date nextRun, boolean isRunning,
            boolean isPaused, String triggerState)
    {
        this.jobName = jobName;
        this.triggerName = triggerName;
        this.lastRun = lastRun;
        this.nextRun = nextRun;
        this.isRunning = isRunning;
        this.isPaused = isPaused;
        this.triggerState = triggerState;
    }

    public String getTriggerName()
    {
        return triggerName;
    }

    public void setTriggerName(String triggerName)
    {
        this.triggerName = triggerName;
    }

    public String getJobName()
    {
        return jobName;
    }

    public void setJobName(String jobName)
    {
        this.jobName = jobName;
    }

    public Long getRepeatIntervalInSeconds()
    {
        return repeatIntervalInSeconds;
    }

    public void setRepeatIntervalInSeconds(Long repeatIntervalInSeconds)
    {
        this.repeatIntervalInSeconds = repeatIntervalInSeconds;
    }

    public String getCronExpression()
    {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression)
    {
        this.cronExpression = cronExpression;
    }

    public boolean isRunning()
    {
        return isRunning;
    }

    public void setRunning(boolean running)
    {
        isRunning = running;
    }

    public Date getLastRun()
    {
        return lastRun;
    }

    public void setLastRun(Date lastRun)
    {
        this.lastRun = lastRun;
    }

    public Date getNextRun()
    {
        return nextRun;
    }

    public void setNextRun(Date nextRun)
    {
        this.nextRun = nextRun;
    }

    public boolean isPaused()
    {
        return isPaused;
    }

    public void setPaused(boolean paused)
    {
        isPaused = paused;
    }

    public String getTriggerState()
    {
        return triggerState;
    }

    public void setTriggerState(String triggerState)
    {
        this.triggerState = triggerState;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AcmJobState acmJobStatus = (AcmJobState) o;
        return Objects.equals(jobName, acmJobStatus.jobName) &&
                Objects.equals(repeatIntervalInSeconds, acmJobStatus.repeatIntervalInSeconds) &&
                Objects.equals(cronExpression, acmJobStatus.cronExpression);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(jobName, repeatIntervalInSeconds, cronExpression);
    }
}
