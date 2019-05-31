package com.armedia.acm.quartz.scheduler;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AcmJobDTO
{
    private String jobName;

    private Long repeatIntervalInSeconds;

    private String cronExpression;

    private Date lastRun;

    private boolean isRunning = false;

    private boolean isPaused = false;

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

    public boolean isPaused()
    {
        return isPaused;
    }

    public Date getLastRun()
    {
        return lastRun;
    }

    public void setLastRun(Date lastRun)
    {
        this.lastRun = lastRun;
    }

    public void setPaused(boolean paused)
    {
        isPaused = paused;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AcmJobDTO acmJobDTO = (AcmJobDTO) o;
        return Objects.equals(jobName, acmJobDTO.jobName) &&
                Objects.equals(repeatIntervalInSeconds, acmJobDTO.repeatIntervalInSeconds) &&
                Objects.equals(cronExpression, acmJobDTO.cronExpression);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(jobName, repeatIntervalInSeconds, cronExpression);
    }
}
