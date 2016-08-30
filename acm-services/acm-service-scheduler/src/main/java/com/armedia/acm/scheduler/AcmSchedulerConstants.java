package com.armedia.acm.scheduler;

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
    String SCHEDULE_INTERVAL_KEY = "scheduleIntervalInMinutes";

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
    String HOW_OFTEN_KEY = "howOftenInMinutes";

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
