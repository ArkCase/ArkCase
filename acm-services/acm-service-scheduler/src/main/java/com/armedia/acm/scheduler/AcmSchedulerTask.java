package com.armedia.acm.scheduler;

import org.springframework.core.task.TaskExecutor;

import java.util.concurrent.CountDownLatch;

/**
 * A simple class that captures data from scheduler configuration needed for running an individual task.
 *
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 24, 2016
 *
 */
public class AcmSchedulerTask
{

    /**
     * How often a task should be run given in milliseconds.
     */
    private volatile long howOften;

    /**
     * When was the task last ran.
     */
    private volatile long taskLastRun;

    /**
     * The Spring bean implementing the <code>AcmSchedulableBean</code> interface.
     *
     * @see AcmSchedulableBean#executeTask()
     */
    private final AcmSchedulableBean schedulableBean;

    /**
     * Constructor for the task.
     *
     * @param howOften
     *            how often the task should be run given in milliseconds.
     * @param taskLastRun
     *            the time when this task was last run.
     * @param schedulableBean
     *            a reference to the Spring bean implementing the <code>AcmSchedulableBean</code> interface, whose
     *            <code>executeTask</code> method will be run by the thread setup by this task.
     * 
     * @see AcmSchedulableBean#executeTask()
     */
    public AcmSchedulerTask(long howOften, long taskLastRun, AcmSchedulableBean schedulableBean)
    {
        this.howOften = howOften;
        this.taskLastRun = taskLastRun;
        this.schedulableBean = schedulableBean;
    }

    /**
     * Adds runnable to the <code>taskExecutoir</code> depending on when the task was last run.
     *
     * @param taskExecutor
     *            executor for the tasks.
     * @param taskCompletedSignal
     *            count down latch for communicating between tasks. When all tasks are executed, the latch counts down
     *            to 0, and the thread for writing off the configuration changes is unblocked.
     */
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

    /**
     * Specifies how often a task should be run.
     *
     * @param howOften
     *            how often a task should be run, given in milliseconds.
     */
    public void setHowOften(long howOften)
    {
        this.howOften = howOften;
    }

    /**
     * Returns the time when the task was last run.
     *
     * @return time when the task was last run in milliseconds since Epoch.
     */
    public long getTaskLastRun()
    {
        return taskLastRun;
    }

}
