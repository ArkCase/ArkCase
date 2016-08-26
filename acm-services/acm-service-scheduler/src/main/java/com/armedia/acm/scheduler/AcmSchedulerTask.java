package com.armedia.acm.scheduler;

import org.springframework.core.task.TaskExecutor;

import java.util.concurrent.CountDownLatch;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 24, 2016
 *
 */
public class AcmSchedulerTask
{
    private volatile long howOften;

    private volatile long taskLastRun;

    private final AcmSchedulableBean schedulableBean;

    public AcmSchedulerTask(long howOften, long taskLastRun, AcmSchedulableBean schedulableBean)
    {
        this.howOften = howOften;
        this.taskLastRun = taskLastRun;
        this.schedulableBean = schedulableBean;
    }

    public void startTask(TaskExecutor taskExecutor, CountDownLatch taskCompletedSignal)
    {
        long now = System.currentTimeMillis();

        if (taskLastRun + howOften <= now)
        {
            taskExecutor.execute(() ->
            {
                try
                {
                    schedulableBean.executeTask();
                } finally
                {
                    taskCompletedSignal.countDown();
                }
            });
            taskLastRun = System.currentTimeMillis();
        } else
        {
            taskCompletedSignal.countDown();
        }
    }

    public void setHowOften(long howOften)
    {
        this.howOften = howOften;
    }

    public long getTaskLastRun()
    {
        return taskLastRun;
    }

}
