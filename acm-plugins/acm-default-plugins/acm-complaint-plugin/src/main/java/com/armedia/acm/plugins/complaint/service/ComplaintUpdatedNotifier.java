package com.armedia.acm.plugins.complaint.service;

/*-
 * #%L
 * ACM Default Plugin: Complaints
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

import com.armedia.acm.core.model.AcmEvent;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.ComplaintModifiedEvent;
import com.armedia.acm.plugins.complaint.model.ComplaintParticipantsModifiedEvent;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.notification.service.NotificationService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;

public class ComplaintUpdatedNotifier implements ApplicationListener<AcmEvent>
{
    private NotificationService notificationService;
    private ComplaintDao complaintDao;

    private static final Logger logger = LogManager.getLogger(ComplaintUpdatedNotifier.class);

    @Override
    public void onApplicationEvent(AcmEvent event)
    {
        String eventType = event.getEventType();

        if (event instanceof ComplaintModifiedEvent)
        {
            Complaint complaint = (Complaint) event.getSource();

            if (eventType.equals("com.armedia.acm.complaint.status.changed"))
            {
                logger.debug("On 'Complaint status changed' event create notification for participants.");

                Notification notification = notificationService.getNotificationBuilder()
                        .newNotification("complaintStatusChanged", NotificationConstants.COMPLAINT_STATUS_CHANGED,
                                complaint.getObjectType(), complaint.getId(), event.getUserId())
                        .forObjectWithNumber(complaint.getComplaintNumber())
                        .forObjectWithTitle(complaint.getTitle())
                        .withEmailAddressesForParticipants(complaint.getParticipants())
                        .build();

                notificationService.saveNotification(notification);
            }
            else if (eventType.equals("com.armedia.acm.complaint.priority.changed"))
            {
                logger.debug("On 'Complaint priority changed' event create notification for participants.");

                Notification notification = notificationService.getNotificationBuilder()
                        .newNotification("complaintPriorityChanged", NotificationConstants.COMPLAINT_PRIORITY_CHANGED,
                                complaint.getObjectType(), complaint.getId(), event.getUserId())
                        .forObjectWithNumber(complaint.getComplaintNumber())
                        .forObjectWithTitle(complaint.getTitle())
                        .withEmailAddressesForParticipants(complaint.getParticipants())
                        .build();

                notificationService.saveNotification(notification);
            }
        }
        else if (event instanceof ComplaintParticipantsModifiedEvent)
        {
            Long complaintId = event.getParentObjectId();

            if (eventType.equals("com.armedia.acm.complaint.participant.added"))
            {
                Complaint complaint = complaintDao.find(complaintId);
                if (complaint != null)
                {
                    logger.debug("On 'Complaint participants added' event create notification for participants.");

                    Notification notification = notificationService.getNotificationBuilder()
                            .newNotification("participantsAdded", NotificationConstants.PARTICIPANTS_ADDED, event.getObjectType(),
                                    event.getObjectId(), event.getUserId())
                            .forRelatedObjectTypeAndId(complaint.getObjectType(), complaint.getId())
                            .forRelatedObjectWithNumber(complaint.getComplaintNumber())
                            .withEmailAddressesForParticipants(complaint.getParticipants())
                            .build();

                    notificationService.saveNotification(notification);
                }
            }
            else if (eventType.equals("com.armedia.acm.complaint.participant.deleted"))
            {
                Complaint complaint = complaintDao.find(complaintId);
                if (complaint != null)
                {
                    logger.debug("On 'Complaint participants deleted' event create notification for participants.");

                    Notification notification = notificationService.getNotificationBuilder()
                            .newNotification("participantsDeleted", NotificationConstants.PARTICIPANTS_DELETED, event.getObjectType(),
                                    event.getObjectId(), event.getUserId())
                            .forRelatedObjectTypeAndId(complaint.getObjectType(), complaint.getId())
                            .forRelatedObjectWithNumber(complaint.getComplaintNumber())
                            .withEmailAddressesForParticipants(complaint.getParticipants())
                            .build();

                    notificationService.saveNotification(notification);
                }
            }
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

    public ComplaintDao getComplaintDao()
    {
        return complaintDao;
    }

    public void setComplaintDao(ComplaintDao complaintDao)
    {
        this.complaintDao = complaintDao;
    }
}
