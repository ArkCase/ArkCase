/**
 *
 */
package com.armedia.acm.services.notification.service;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.services.notification.dao.NotificationDao;
import com.armedia.acm.services.notification.model.ApplicationNotificationEvent;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.notification.model.NotificationRule;
import com.armedia.acm.spring.SpringContextHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author riste.tutureski
 */
public class NotificationServiceImpl implements NotificationService
{

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private boolean batchRun;
    private int batchSize;
    private int purgeDays;
    private PropertyFileManager propertyFileManager;
    private String notificationPropertyFileLocation;
    private NotificationDao notificationDao;
    private NotificationEventPublisher notificationEventPublisher;
    private SpringContextHolder springContextHolder;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private NotificationFormatter notificationFormatter;

    /**
     * This method is called by scheduled task
     */
    @Override
    public void run()
    {
        if (!isBatchRun())
        {
            return;
        }

        getAuditPropertyEntityAdapter().setUserId(NotificationConstants.SYSTEM_USER);

        LOG.debug("Getting last batch run date from {}", NotificationConstants.LAST_BATCH_RUN_PROPERTY_FILE);

        try
        {
            String lastRunDate = getPropertyFileManager().load(NotificationConstants.LAST_BATCH_RUN_PROPERTY_FILE,
                    NotificationConstants.SOLR_LAST_RUN_DATE_PROPERTY_KEY, NotificationConstants.DEFAULT_LAST_RUN_DATE);

            SimpleDateFormat dateFormat = new SimpleDateFormat(NotificationConstants.DATE_FORMAT);

            Date lastRun = getLastRunDate(lastRunDate, dateFormat);
            setLastRunDate(dateFormat);

            Map<String, NotificationRule> rules = getSpringContextHolder().getAllBeansOfType(NotificationRule.class);

            if (rules != null)
            {
                for (NotificationRule rule : rules.values())
                {
                    runRule(lastRun, rule);
                }
            }

        }
        catch (Exception e)
        {
            LOG.error("Cannot send notifications to the users: " + e.getMessage(), e);
        }
    }

    /**
     * This method is called for executing the rule query and sending notifications
     */
    @Override
    public void runRule(Date lastRun, NotificationRule rule)
    {
        int firstResult = 0;
        int maxResult = getBatchSize();

        List<Notification> notifications;

        do
        {
            Map<String, Object> properties = getJpaProperties(rule, lastRun);
            notifications = getNotificationDao().executeQuery(properties, firstResult, maxResult, rule.getJpaQuery(), rule.getQueryType());

            if (!notifications.isEmpty())
            {
                firstResult += maxResult;

                notifications.stream().map(element -> getNotificationFormatter().replaceFormatPlaceholders(element))
                        .map(element -> rule.getExecutor().execute(element)).map(element -> getNotificationDao().save(element))
                        .forEach(element -> {
                            ApplicationNotificationEvent event = new ApplicationNotificationEvent(element,
                                    NotificationConstants.OBJECT_TYPE.toLowerCase(), true, null);
                            getNotificationEventPublisher().publishNotificationEvent(event);
                        });
            }
        }
        while (!notifications.isEmpty());
    }

    /**
     * Get the last run date corrected for 1 minute before
     *
     * @param lastRunDate
     * @return
     * @throws ParseException
     */
    private Date getLastRunDate(String lastRunDate, SimpleDateFormat dateFormat) throws ParseException
    {
        Date date = dateFormat.parse(lastRunDate);

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - 1);

        date = calendar.getTime();

        return date;
    }

    /**
     * Save last run date in the properties file
     *
     * @param dateFormat
     */
    private void setLastRunDate(SimpleDateFormat dateFormat)
    {
        String lastRunDate = dateFormat.format(new Date());

        Map<String, String> properties = new HashMap<>();
        properties.put(NotificationConstants.SOLR_LAST_RUN_DATE_PROPERTY_KEY, lastRunDate);

        getPropertyFileManager().storeMultiple(properties, NotificationConstants.LAST_BATCH_RUN_PROPERTY_FILE, true);
    }

    /**
     * Create needed JPA query properties. In the DAO we have logic which one should be excluded
     *
     * @param rule
     * @param lastRun
     * @return
     */
    private Map<String, Object> getJpaProperties(NotificationRule rule, Date lastRun)
    {
        Map<String, Object> jpaProperties = rule.getJpaProperties();

        if (jpaProperties == null)
        {
            jpaProperties = new HashMap<>();
        }

        jpaProperties.put("lastRunDate", lastRun);
        jpaProperties.put("threshold", createPurgeThreshold());

        return jpaProperties;
    }

    /**
     * Create purge date threshold: today-days
     *
     * @return
     */
    private Date createPurgeThreshold()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -getPurgeDays());

        return calendar.getTime();
    }

    public boolean isBatchRun()
    {
        return batchRun;
    }

    public void setBatchRun(boolean batchRun)
    {
        this.batchRun = batchRun;
    }

    public int getBatchSize()
    {
        return batchSize;
    }

    public void setBatchSize(int batchSize)
    {
        this.batchSize = batchSize;
    }

    public int getPurgeDays()
    {
        return purgeDays;
    }

    public void setPurgeDays(int purgeDays)
    {
        this.purgeDays = purgeDays;
    }

    public PropertyFileManager getPropertyFileManager()
    {
        return propertyFileManager;
    }

    public void setPropertyFileManager(PropertyFileManager propertyFileManager)
    {
        this.propertyFileManager = propertyFileManager;
    }

    public String getNotificationPropertyFileLocation()
    {
        return notificationPropertyFileLocation;
    }

    public void setNotificationPropertyFileLocation(String notificationPropertyFileLocation)
    {
        this.notificationPropertyFileLocation = notificationPropertyFileLocation;
    }

    public NotificationDao getNotificationDao()
    {
        return notificationDao;
    }

    public void setNotificationDao(NotificationDao notificationDao)
    {
        this.notificationDao = notificationDao;
    }

    public NotificationEventPublisher getNotificationEventPublisher()
    {
        return notificationEventPublisher;
    }

    public void setNotificationEventPublisher(NotificationEventPublisher notificationEventPublisher)
    {
        this.notificationEventPublisher = notificationEventPublisher;
    }

    public SpringContextHolder getSpringContextHolder()
    {
        return springContextHolder;
    }

    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

    public NotificationFormatter getNotificationFormatter()
    {
        return notificationFormatter;
    }

    public void setNotificationFormatter(NotificationFormatter notificationFormatter)
    {
        this.notificationFormatter = notificationFormatter;
    }
}
