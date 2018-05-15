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
 * A non functional interface that contains constants used by the scheduler infrastructure for processing the scheduler
 * configuration.
 *
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 25, 2016
 *
 */
public interface AcmSchedulerConstants
{

    /**
     * The scheduler task name.
     */
    String SCHEDULED_TASKS_CONFIGUTATION_FILENAME = "scheduledTasks.json";

    /**
     * Key in the scheduler configuration that specifies if scheduler is enabled.
     */
    String SCHEDULE_ENABLED_KEY = "scheduleEnabled";

    /**
     * Key in the scheduler configuration that specifies the interval at which the scheduler thread should be invoked.
     */
    String SCHEDULE_INTERVAL_KEY = "scheduleIntervalInSeconds";

    /**
     * Key in the scheduler configuration that specifies the tasks configuration.
     */
    String TASKS_KEY = "tasks";

    /**
     * Key in the task configuration that specifies the task name.
     */
    String NAME_KEY = "name";

    /**
     * Key in the task configuration that specifies how often a task should be run.
     */
    String HOW_OFTEN_KEY = "howOftenInSeconds";

    /**
     * Key in the task configuration that specifies when was the last time a task was run.
     */
    String TASK_LAST_RUN_KEY = "taskLastRun";

    /**
     * Key in the task configuration that specifies the name (id) under which a bean is registered in the Spring
     * context.
     */
    String BEAN_NAME_KEY = "beanName";

}
