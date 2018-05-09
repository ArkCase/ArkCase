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
     * @see AcmSchedulerTask#startTask(String, org.springframework.core.task.TaskExecutor,
     *      java.util.concurrent.CountDownLatch)
     */
    void executeTask();
}
