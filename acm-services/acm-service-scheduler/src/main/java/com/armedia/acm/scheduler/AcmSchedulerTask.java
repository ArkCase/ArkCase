package com.armedia.acm.scheduler;

/*-
 * #%L
 * ACM Service: Scheduler Service
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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

    private transient final Logger log = LogManager.getLogger(getClass());
    /**
     * The Spring bean implementing the <code>AcmSchedulableBean</code> interface.
     *
     * @see AcmSchedulableBean#executeTask()
     */
    private final AcmSchedulableBean schedulableBean;
    /**
     * How often a task should be run given in milliseconds.
     */
    private volatile long howOften;
    /**
     * When was the task last ran.
     */
    private volatile long taskLastRun;

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
     * @param taskName
     *            name of the task.
     * @param taskExecutor
     *            executor for the tasks.
     * @param taskCompletedSignal
     *            count down latch for communicating between tasks. When all tasks are executed, the latch counts down
     *            to 0, and the thread for writing off the configuration changes is unblocked.
     */
    public void startTask(String taskName, TaskExecutor taskExecutor, CountDownLatch taskCompletedSignal)
    {
        long now = System.currentTimeMillis();
        log.debug("Starting task [{}] at [{}]", taskName, now);

        if (taskLastRun + howOften <= now)
        {
            log.debug("Submitting task [{}] for execution.", taskName);
            taskExecutor.execute(() -> {
                try
                {
                    schedulableBean.executeTask();
                }
                finally
                {
                    taskCompletedSignal.countDown();
                    log.debug("Finished executing task [{}].", taskName);
                }
            });
            taskLastRun = System.currentTimeMillis();
            log.debug("Task [{}] last run at [{}]", taskName, taskLastRun);
        }
        else
        {
            log.debug("Task [{}] was not submitted for execution.", taskName);
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
