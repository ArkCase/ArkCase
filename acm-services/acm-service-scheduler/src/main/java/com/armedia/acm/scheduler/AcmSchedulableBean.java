package com.armedia.acm.scheduler;

/**
 * Interface that should be implemented by beans intended to be run by the scheduler infrastructure defined by
 * <code>AcmScheduler</code> and <code>AcmSchedulerTask</code>.
 *
 * @see AcmScheduler
 * @see AcmSchedulerTask
 *
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 25, 2016
 *
 */
public interface AcmSchedulableBean
{
    /**
     * A task that is invoked by the thread that <code>AcmSchedulerTask</code> submits to the tasks executor.
     *
     * @see AcmSchedulerTask#startTask(String, org.springframework.core.task.TaskExecutor, java.util.concurrent.CountDownLatch)
     */
    void executeTask();
}
