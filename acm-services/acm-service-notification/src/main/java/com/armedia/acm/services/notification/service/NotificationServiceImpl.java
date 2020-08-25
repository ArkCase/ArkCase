package com.armedia.acm.services.notification.service;

/*-
 * #%L
 * ACM Service: Notification
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

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.services.notification.dao.NotificationDao;
import com.armedia.acm.services.notification.model.ApplicationNotificationEvent;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConfig;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.notification.model.NotificationRule;
import com.armedia.acm.spring.SpringContextHolder;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationServiceImpl implements NotificationService
{
    private final Logger LOG = LogManager.getLogger(getClass());

    private NotificationConfig notificationConfig;
    private NotificationDao notificationDao;
    private NotificationEventPublisher notificationEventPublisher;
    private SpringContextHolder springContextHolder;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private NotificationFormatter notificationFormatter;
    private NotificationBuilder notificationBuilder;

    /**
     * This method is called by scheduled task
     */
    @Override
    public void run(Date lastRun)
    {
        if (!notificationConfig.getUserBatchRun())
        {
            return;
        }

        getAuditPropertyEntityAdapter().setUserId(NotificationConstants.SYSTEM_USER);

        try
        {
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
            LOG.error("Cannot send notifications to the users: {}", e.getMessage(), e);
        }
    }

    /**
     * This method is called for executing the rule query and sending notifications
     */
    @Override
    public void runRule(Date lastRun, NotificationRule rule)
    {
        int firstResult = 0;
        int maxResult = notificationConfig.getUserBatchSize();

        List<Notification> notifications;

        do
        {
            Map<String, Object> properties = getJpaProperties(rule, lastRun);
            notifications = getNotificationDao().executeQuery(properties, firstResult, maxResult, rule);

            if (!notifications.isEmpty())
            {
                firstResult += maxResult;

                notifications.stream()
                        .map(element -> rule.getExecutor().execute(element))
                        .map(element -> getNotificationDao().save(element))
                        .forEach(element -> {
                            ApplicationNotificationEvent event = new ApplicationNotificationEvent(element,
                                    NotificationConstants.OBJECT_TYPE.toLowerCase(), true, null);
                            getNotificationEventPublisher().publishNotificationEvent(event);
                        });
            }
        } while (!notifications.isEmpty());
    }

    @Override
    public NotificationBuilder getNotificationBuilder()
    {
        return notificationBuilder;
    }

    @Override
    public Notification saveNotification(Notification notification)
    {
        if (StringUtils.isBlank(notification.getEmailAddresses()))
        {
            LOG.warn("Notification with template [{}], for object [{}] with id [{}] won't be created for empty email addresses list.",
                    notification.getTemplateModelName(), notification.getParentType(), notification.getParentId());
            return null;
        }

        Notification saved = notificationDao.save(notification);
        LOG.debug("Created notification with template [{}], for object [{}] with id [{}] and email addresses [{}].",
                notification.getTemplateModelName(), notification.getParentType(), notification.getParentId(),
                notification.getEmailAddresses());

        return saved;
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
        calendar.add(Calendar.DATE, -notificationConfig.getPurgeDays());

        return calendar.getTime();
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

    public NotificationConfig getNotificationConfig()
    {
        return notificationConfig;
    }

    public void setNotificationConfig(NotificationConfig notificationConfig)
    {
        this.notificationConfig = notificationConfig;
    }

    public void setNotificationBuilder(NotificationBuilder notificationBuilder)
    {
        this.notificationBuilder = notificationBuilder;
    }
}
