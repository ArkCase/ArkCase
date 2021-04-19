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

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class NotificationServiceImpl implements NotificationService
{
    private final Logger LOG = LogManager.getLogger(getClass());

    private NotificationConfig notificationConfig;
    private NotificationDao notificationDao;
    private NotificationEventPublisher notificationEventPublisher;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private NotificationBuilder notificationBuilder;
    private SendExecutor sendExecutor;

    /**
     * This method is called by scheduled task
     */
    @Override
    public void run()
    {
        if (!notificationConfig.getUserBatchRun())
        {
            LOG.info("Notifications won't be processed since batch run is disabled");
            return;
        }

        getAuditPropertyEntityAdapter().setUserId(NotificationConstants.SYSTEM_USER);

        try
        {
            List<Notification> notificationToProcess = notificationDao.getNotificationsToProcess();

            notificationToProcess.stream()
                    .map(element -> getSendExecutor().execute(element))
                    .map(element -> getNotificationDao().save(element))
                    .forEach(element -> {
                        ApplicationNotificationEvent event = new ApplicationNotificationEvent(element,
                                NotificationConstants.OBJECT_TYPE.toLowerCase(), true, null);
                        getNotificationEventPublisher().publishNotificationEvent(event);
                    });
        }
        catch (Exception e)
        {
            LOG.error("Cannot send notifications to the users: {}", e.getMessage(), e);
        }
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

    @Override
    public String setNotificationTitleForManualNotification(String templateName)
    {
        String title = "";
        switch (templateName)
        {
        case "casePriorityChanged":
            title = NotificationConstants.CASE_PRIORITY_CHANGED;
            break;
        case "caseStatusChanged":
            title = NotificationConstants.CASE_STATUS_CHANGED;
            break;
        case "complaintPriorityChanged":
            title = NotificationConstants.COMPLAINT_PRIORITY_CHANGED;
            break;
        case "complaintStatusChanged":
            title = NotificationConstants.COMPLAINT_STATUS_CHANGED;
            break;
        case "mentions":
            title = NotificationConstants.EMAIL_MENTIONS;
            break;
        case "noteAdded":
            title = NotificationConstants.NOTE_ADDED;
            break;
        case "objectAssigned":
            title = NotificationConstants.OBJECT_ASSIGNED;
            break;
        case "objectUnassigned":
            title = NotificationConstants.OBJECT_UNASSIGNED;
            break;
        case "participantsAdded":
            title = NotificationConstants.PARTICIPANTS_ADDED;
            break;
        case "participantsDeleted":
            title = NotificationConstants.PARTICIPANTS_DELETED;
            break;
        case "taskOverdue":
            title = NotificationConstants.TASK_OVERDUE;
            break;
        case "taskPriorityChanged":
            title = NotificationConstants.TASK_PRIORITY_CHANGED;
            break;
        case "taskStatusChanged":
            title = NotificationConstants.TASK_STATUS_CHANGED;
            break;
        case "taskUpcoming":
            title = NotificationConstants.TASK_UPCOMING;
            break;
        case "concurNonConcur":
            title = NotificationConstants.TASK_CONCUR_NONCONCUR;
            break;
        case "requestReleased":
            title = NotificationConstants.REQUEST_RELEASED;
            break;
        }
        return title;
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

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
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

    public SendExecutor getSendExecutor()
    {
        return sendExecutor;
    }

    public void setSendExecutor(SendExecutor sendExecutor)
    {
        this.sendExecutor = sendExecutor;
    }
}
