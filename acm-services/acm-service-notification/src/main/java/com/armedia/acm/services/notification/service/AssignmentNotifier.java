package com.armedia.acm.services.notification.service;

/*-
 * #%L
 * ACM Service: Notification
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import com.armedia.acm.service.objecthistory.model.AcmAssigneeChangeEvent;
import com.armedia.acm.service.objecthistory.model.AcmAssignment;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;

public class AssignmentNotifier implements ApplicationListener<AcmAssigneeChangeEvent>
{
    private NotificationService notificationService;

    private static final Logger logger = LogManager.getLogger(AssignmentNotifier.class);

    @Override
    public void onApplicationEvent(AcmAssigneeChangeEvent event)
    {
        AcmAssignment assignment = (AcmAssignment) event.getSource();

        String newAssignee = assignment.getNewAssignee();
        String oldAssignee = assignment.getOldAssignee();

        if (StringUtils.isNotBlank(newAssignee) && !newAssignee.equals("None"))
        {
            logger.debug("On 'Assignment changed' event create notification for new assignee [{}].", newAssignee);

            Notification notification = notificationService.getNotificationBuilder()
                    .newNotification("objectAssigned", NotificationConstants.OBJECT_ASSIGNED, assignment.getObjectType(),
                            assignment.getObjectId(), event.getUserId())
                    .forObjectWithNumber(assignment.getObjectName())
                    .forObjectWithTitle(assignment.getObjectTitle())
                    .withEmailAddressForUser(newAssignee)
                    .build(newAssignee);

            notificationService.saveNotification(notification);
        }

        if (StringUtils.isNotBlank(oldAssignee))
        {
            logger.debug("On 'Assignment changed' event create notification for old assignee [{}].", oldAssignee);

            Notification notification = notificationService.getNotificationBuilder()
                    .newNotification("objectUnassigned", NotificationConstants.OBJECT_UNASSIGNED, assignment.getObjectType(),
                            assignment.getObjectId(), event.getUserId())
                    .forObjectWithNumber(assignment.getObjectName())
                    .forObjectWithTitle(assignment.getObjectTitle())
                    .withEmailAddressForUser(oldAssignee)
                    .build(oldAssignee);

            notificationService.saveNotification(notification);
        }
    }

    public NotificationService getNotificationService()
    {
        return notificationService;
    }

    public void setNotificationService(NotificationService notificationService)
    {
        this.notificationService = notificationService;
    }
}
