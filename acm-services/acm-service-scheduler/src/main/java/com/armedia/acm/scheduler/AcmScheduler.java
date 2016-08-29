package com.armedia.acm.scheduler;

import static com.armedia.acm.scheduler.AcmSchedulerConstants.BEAN_NAME_KEY;
import static com.armedia.acm.scheduler.AcmSchedulerConstants.HOW_OFTEN_KEY;
import static com.armedia.acm.scheduler.AcmSchedulerConstants.NAME_KEY;
import static com.armedia.acm.scheduler.AcmSchedulerConstants.SCHEDULED_TASKS_CONFIGUTATION_FILENAME;
import static com.armedia.acm.scheduler.AcmSchedulerConstants.SCHEDULE_ENABLED_KEY;
import static com.armedia.acm.scheduler.AcmSchedulerConstants.SCHEDULE_INTERVAL_KEY;
import static com.armedia.acm.scheduler.AcmSchedulerConstants.TASKS_KEY;
import static com.armedia.acm.scheduler.AcmSchedulerConstants.TASK_LAST_RUN_KEY;

import com.armedia.acm.files.AbstractConfigurationFileEvent;
import com.armedia.acm.files.ConfigurationFileAddedEvent;
import com.armedia.acm.files.ConfigurationFileChangedEvent;
import com.armedia.acm.spring.SpringContextHolder;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * A generic configurable scheduler capable of executing beans defined in spring context that implement the
 * <code>AcmSchedulableBean</code> interface. The scheduler listens for changes in the <code>scheduledTasks.json</code>
 * file that contains the scheduler and scheduled tasks configuration. A sample JSON configuration file: <code>
   {
    "scheduleEnabled": "true",
    "scheduleInterval": "1",
    "tasks": [{
        "howOften": "5",
        "name": "billingQueuePurger",
        "beanName": "scheduledBillingQueuePurger"
    },{
        "howOften": "10",
        "name": "queueLogger",
        "beanName": "scheduledQueueLogger"
    }]
   }
 * </code>
 *
 * @see AcmSchedulableBean#executeTask()
 *
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 24, 2016
 */
public class AcmScheduler implements ApplicationListener<AbstractConfigurationFileEvent>
{

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Task scheduler contains a single thread that runs periodically and is responsible for assigning tasks to the
     * <code>tasksExecutor</code>.
     */
    private TaskScheduler taskScheduler;

    /**
     * Runs threads that are started by <code>AcmSchedulerTask</code>'s <code>startTask</code> method.
     *
     * @see AcmSchedulerTask#startTask(TaskExecutor, CountDownLatch)
     */
    private TaskExecutor taskExecutor;

    /**
     * An instance of the <code>SpringContextHolder</code> is needed for resolving beans implementing the
     * <code>SchedulableBean</code> interface by name. The bean names are provided in the configuration of this
     * scheduler. Beans are then injected in the constructor of the <code>AcmSchedulerTask</code>.
     *
     * @see AcmSchedulableBean
     * @see AcmSchedulerTask#AcmSchedulerTask(long, long, AcmSchedulableBean)
     */
    private SpringContextHolder springContextHolder;

    /**
     * Path to the configuration file is stored because it is needed to make it possible to update the configuration
     * upon finishing the scheduled tasks.
     */
    private String configurationPath;

    /**
     * The interval at which the scheduler is run is stored in order to determine if it needs to be restarted upon
     * configuration update.
     */
    private long scheduleInterval;

    /**
     * Task name to task mapping.
     */
    private Map<String, AcmSchedulerTask> tasks = new HashMap<>();

    /**
     * A reference to a
     * <code>ScheduledFuture<code> that is returned upon scheduling the scheduler thread is stored in order
     * to be possible to cancel it in case the value of the <code>scheduleInterval</code> is changed in the
     * configuration.
     */
    private ScheduledFuture<?> taskSchedulerFuture;

    /**
     * Stores the time of last modification of the configuration file in milliseconds since Epoch. This is needed since
     * the configuration is changed by this <code>AcmScheduler</code> when all tasks are finished with the values of the
     * last time when the respective tasks were ran. This will trigger the <code>onApplicationEvent</code> method, which
     * we don't want to execute in case the configuration file change was triggered by this scheduler.
     *
     * @see #onApplicationEvent(AbstractConfigurationFileEvent)
     * @see #updateCоnfiguration()
     */
    private long lastModifiedTime;

    /**
     * Constructs a new instance of the scheduler.
     *
     * @param taskScheduler
     *            has one thread that is run periodically in order to submit tasks to the <code>taskExecutor</code>. The
     *            frequency at which the scheduler is run is defined in the JSON configuration file.
     * @param taskExecutor
     *            executes individual tasks submitted by the task scheduling thread. Individual tasks are defined in the
     *            JSON configuration file in a JSON array containing JSON object defining individual tasks.
     * @param springContextHolder
     *            needed for obtaining instances of Spring beans by name. Beans defined in the tasks section of the JSON
     *            configuration file must implement the <code>AcmSchedulableBean</code> interface.
     *
     * @see AcmSchedulerConstants#SCHEDULE_INTERVAL_KEY for the key value in the JSON configuration representing the
     *      frequency at which the scheduler is run.
     * @see AcmSchedulerConstants#TASKS_KEY for the key value in the JSON configuration refering to the JSON array
     *      containing the configuration of individual tasks.
     * @see AcmSchedulerConstants#BEAN_NAME_KEY for the key name in the JSON configuration file under the tasks section
     *      that defines the bean name implementing the <code>AcmSchedulableBean</code> interface that should be
     *      executed by the task.
     */
    public AcmScheduler(TaskScheduler taskScheduler, TaskExecutor taskExecutor, SpringContextHolder springContextHolder)
    {
        this.taskScheduler = taskScheduler;
        this.taskExecutor = taskExecutor;
        this.springContextHolder = springContextHolder;
    }

    @Override
    public void onApplicationEvent(AbstractConfigurationFileEvent event)
    {

        if (isConfigurationFileChange(event))
        {
            File configFile = event.getConfigFile();
            try
            {
                synchronized (this)
                {

                    FileTime lastModified = getConfigLastModifiedTime(configFile);
                    // configuration change that originated by update from this scheduler should not be processed.
                    if (lastModifiedTime == lastModified.toMillis())
                    {
                        return;
                    }

                    processSchedulerConfiguration(configFile);
                }
            } catch (IOException | JSONException e)
            {
                log.error("Could not load scheduler configuration from file {}, error was: {}.", configFile.getAbsoluteFile(), e);
            }
        }

    }

    /**
     * Checks if the event was triggered by a change of the scheduler configuration file.
     *
     * @param abstractConfigurationFileEvent
     *            the event encapsulating a reference to the modified file.
     * @return <code>true</code> if the event was triggered by the scheduler configuration <code>false</code> otherwise.
     */
    private boolean isConfigurationFileChange(AbstractConfigurationFileEvent abstractConfigurationFileEvent)
    {
        return (abstractConfigurationFileEvent instanceof ConfigurationFileAddedEvent
                || abstractConfigurationFileEvent instanceof ConfigurationFileChangedEvent)
                && abstractConfigurationFileEvent.getConfigFile().getName().equals(SCHEDULED_TASKS_CONFIGUTATION_FILENAME);
    }

    /**
     * Extracts the file last modified time.
     *
     * @param configFile
     *            a reference to the configuration file.
     * @return a reference to an instance of <code>FileTime</code> associated with the scheduler configuration time
     *         referring to its last modification time.
     * @throws IOException
     *             if there is a problem while reading configuration file attributes.
     */
    private FileTime getConfigLastModifiedTime(File configFile) throws IOException
    {
        Path configFilePath = configFile.toPath();
        BasicFileAttributes attributes = Files.readAttributes(configFilePath, BasicFileAttributes.class);
        FileTime lastModified = attributes.lastModifiedTime();
        return lastModified;
    }

    /**
     * Processes the scheduler configuration file. It stores a reference to the path to the configuration path, so it
     * can be updated upon tasks completion. Checks if the scheduler is enabled or not. If it is not, and the scheduler
     * was setup before, it attempts to stop it. If it is, it processes the individual tasks configuration, and sets up
     * the scheduler if one is not running already, or its configuration changed.
     *
     * @param configFile
     *            a reference to the scheduler configuration file.
     * @throws IOException
     *             if there was an error while reading the configuration file.
     */
    private void processSchedulerConfiguration(File configFile) throws IOException
    {
        String resource = FileUtils.readFileToString(configFile);
        configurationPath = configFile.getAbsolutePath();
        JSONObject configuration = new JSONObject(resource);

        if (!configuration.getBoolean(SCHEDULE_ENABLED_KEY))
        {
            if (taskSchedulerFuture != null)
            {
                taskSchedulerFuture.cancel(false);
                taskSchedulerFuture = null;
            }
            scheduleInterval = 0;
            tasks.clear();
            return;
        }

        processTasksConfigurations(configuration);
        setupScheduler(configuration);
    }

    /**
     * Processes the tasks configurations. The tasks configuration are stored in the configuration file under the
     * <code>tasks</code> key. If the configuration contains a task configuration not already configured and stored in
     * the <code>tasks</code> map, a new instance of <code>AcmSchedulerTask</code> is created and stored in the
     * <code>tasks</code> map with key value retrieved from the <code>name</code> element in the task configuration. If
     * it was already configured, than it is updated. Tasks that were previously configured, but are no longer in the
     * configuration are removed from the <code>tasks</code> map.
     *
     * @param configuration
     *            JSON object that was created by parsing the contents of the configuration file.
     *
     * @see AcmSchedulerConstants#TASKS_KEY
     * @see #tasks
     * @see AcmSchedulerConstants#NAME_KEY
     */
    private void processTasksConfigurations(JSONObject configuration)
    {
        JSONArray tasksConfigurations = configuration.getJSONArray(TASKS_KEY);

        Set<String> keys = new HashSet<>();

        for (int i = 0; i < tasksConfigurations.length(); i++)
        {
            JSONObject taskConfiguration = tasksConfigurations.getJSONObject(i);
            String taskName = taskConfiguration.getString(NAME_KEY);
            // how often in the configuration is given in minutes, needs to be converted in milliseconds.
            long howOften = taskConfiguration.getLong(HOW_OFTEN_KEY) * 60 * 1000;
            if (tasks.containsKey(taskName))
            {
                AcmSchedulerTask task = tasks.get(taskName);
                task.setHowOften(howOften);
            } else
            {
                String beanName = taskConfiguration.getString(BEAN_NAME_KEY);
                try
                {
                    AcmSchedulableBean schedulableBean = springContextHolder.getBeanByName(beanName, AcmSchedulableBean.class);
                    AcmSchedulerTask task = new AcmSchedulerTask(howOften,
                            taskConfiguration.has(TASK_LAST_RUN_KEY) ? taskConfiguration.getLong(TASK_LAST_RUN_KEY) : 0, schedulableBean);
                    keys.add(taskName);
                    tasks.put(taskName, task);
                } catch (NoSuchBeanDefinitionException | BeanNotOfRequiredTypeException e)
                {
                    log.error("Either bean with name {} does not exist or is not of type {}SchedulableBean. Exception {}.", beanName,
                            AcmSchedulableBean.class.getName(), e);
                }
            }
        }

        // remove all tasks that are no longer in the configuration.
        tasks.keySet().retainAll(keys);
    }

    /**
     * Sets up the scheduler. The scheduler has a single thread that runs periodically and submits tasks to the
     * executor. In case a scheduler was already setup, and the value in the configuration specifying how often is
     * should be run has changed, it is canceled and rescheduled.
     *
     * @param configuration
     *            JSON object that was created by parsing the contents of the configuration file.
     */
    private void setupScheduler(JSONObject configuration)
    {
        // the interval in the configuration is given in minutes, needs to be converted in milliseconds.
        long scheduleInterval = configuration.getLong(SCHEDULE_INTERVAL_KEY) * 60 * 1000;

        if (scheduleInterval != this.scheduleInterval)
        {
            this.scheduleInterval = scheduleInterval;

            if (taskSchedulerFuture != null)
            {
                taskSchedulerFuture.cancel(false);
            }

            Runnable scheduler = schedulerRunnable(scheduleInterval);

            taskSchedulerFuture = taskScheduler.scheduleAtFixedRate(scheduler, scheduleInterval);
        }
    }

    /**
     * Creates a runnable to be submitted to the scheduler.
     *
     * @param scheduleInterval
     *            the interval at which the scheduler runs periodically.
     * @return the runnable to be submitted to the scheduler.
     */
    private Runnable schedulerRunnable(long scheduleInterval)
    {
        return () ->
        {

            CountDownLatch taskCompletedSignal = new CountDownLatch(tasks.size());
            for (AcmSchedulerTask task : tasks.values())
            {
                task.startTask(taskExecutor, taskCompletedSignal);
            }

            Runnable configurationUpdater = () ->
            {
                try
                {
                    taskCompletedSignal.await(scheduleInterval, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e)
                {
                    log.error("Configuration update thread was interrupted prematurelly with exception {}.", e);
                } finally
                {
                    updateCоnfiguration();
                }
            };
            taskExecutor.execute(configurationUpdater);

        };
    }

    /**
     * Updates the scheduler configuration upon completion of all tasks. What is updated are the values of the
     * <code>taskLastRun</code> field of the individual tasks. After the configuration file has been written to the file
     * system, the modification time is recorded.
     *
     * @see AcmSchedulerConstants#TASK_LAST_RUN_KEY
     */
    private synchronized void updateCоnfiguration()
    {
        if (taskSchedulerFuture == null)
        {
            return;
        }
        File configFile = FileUtils.getFile(configurationPath);
        try
        {

            String resource = FileUtils.readFileToString(configFile);
            JSONObject configuration = new JSONObject(resource);
            JSONArray tasksConfigurations = configuration.getJSONArray(TASKS_KEY);

            for (int i = 0; i < tasksConfigurations.length(); i++)
            {

                JSONObject taskConfiguration = tasksConfigurations.getJSONObject(i);
                String taskName = taskConfiguration.getString(NAME_KEY);

                long taskLastRun = tasks.get(taskName).getTaskLastRun();
                taskConfiguration.put(TASK_LAST_RUN_KEY, taskLastRun);

            }

            FileUtils.write(configFile, configuration.toString());

            FileTime lastModified = getConfigLastModifiedTime(configFile);

            lastModifiedTime = lastModified.toMillis();

        } catch (IOException | JSONException e)
        {
            log.error("Could not write scheduler configuration to file {}, error was: {}.", configurationPath, e);
        }
    }

}
