package com.armedia.acm.plugins.consultation.service;

/*-
 * #%L
 * ACM Default Plugin: Consultation
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
import com.armedia.acm.plugins.consultation.dao.ConsultationDao;
import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.plugins.consultation.model.ConsultationModifiedEvent;
import com.armedia.acm.plugins.consultation.model.ConsultationParticipantsModifiedEvent;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.notification.service.NotificationService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;

public class ConsultationUpdatedNotifier implements ApplicationListener<AcmEvent>
{
    private NotificationService notificationService;
    private ConsultationDao consultationDao;

    private static final Logger logger = LogManager.getLogger(ConsultationUpdatedNotifier.class);

    @Override
    public void onApplicationEvent(AcmEvent event)
    {
        String eventType = event.getEventType();

        if (event instanceof ConsultationModifiedEvent)
        {
            Consultation consultation = (Consultation) event.getSource();

            if (eventType.equals("com.armedia.acm.consultation.status.changed"))
            {
                logger.debug("On 'Consultation status changed' event create notification for participants.");

                Notification notification = notificationService.getNotificationBuilder()
                        .newNotification("consultationStatusChanged", NotificationConstants.CONSULTATION_STATUS_CHANGED, consultation.getObjectType(),
                                consultation.getId(), event.getUserId())
                        .forObjectWithNumber(consultation.getConsultationNumber())
                        .forObjectWithTitle(consultation.getTitle())
                        .withEmailAddressesForParticipants(consultation.getParticipants())
                        .build();

                notificationService.saveNotification(notification);
            }
            else if (eventType.equals("com.armedia.acm.consultation.priority.changed"))
            {
                logger.debug("On 'Consultation priority changed' event create notification for participants.");

                Notification notification = notificationService.getNotificationBuilder()
                        .newNotification("consultationPriorityChanged", NotificationConstants.CONSULTATION_PRIORITY_CHANGED, consultation.getObjectType(),
                                consultation.getId(), event.getUserId())
                        .forObjectWithNumber(consultation.getConsultationNumber())
                        .forObjectWithTitle(consultation.getTitle())
                        .withEmailAddressesForParticipants(consultation.getParticipants())
                        .build();

                notificationService.saveNotification(notification);
            }
        }
        else if (event instanceof ConsultationParticipantsModifiedEvent)
        {
            Long consultationId = event.getParentObjectId();

            if (eventType.equals("com.armedia.acm.consultation.participant.added"))
            {

                Consultation consultation = consultationDao.find(consultationId);
                if (consultation != null)
                {
                    logger.debug("On 'Consultation participants added' event create notification for participants.");

                    Notification notification = notificationService.getNotificationBuilder()
                            .newNotification("participantsAdded", NotificationConstants.PARTICIPANTS_ADDED, event.getObjectType(),
                                    event.getObjectId(), event.getUserId())
                            .forRelatedObjectTypeAndId(consultation.getObjectType(), consultation.getId())
                            .forRelatedObjectWithNumber(consultation.getConsultationNumber())
                            .withEmailAddressesForParticipants(consultation.getParticipants())
                            .build();

                    notificationService.saveNotification(notification);
                }

            }
            else if (eventType.equals("com.armedia.acm.consultation.participant.deleted"))
            {
                Consultation consultation = consultationDao.find(consultationId);
                if (consultation != null)
                {
                    logger.debug("On 'Consultation participants deleted' event create notification for participants.");

                    Notification notification = notificationService.getNotificationBuilder()
                            .newNotification("participantsDeleted", NotificationConstants.PARTICIPANTS_DELETED, event.getObjectType(),
                                    event.getObjectId(), event.getUserId())
                            .forRelatedObjectTypeAndId(consultation.getObjectType(), consultation.getId())
                            .forRelatedObjectWithNumber(consultation.getConsultationNumber())
                            .withEmailAddressesForParticipants(consultation.getParticipants())
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

    public ConsultationDao getConsultationDao() {
        return consultationDao;
    }

    public void setConsultationDao(ConsultationDao consultationDao) {
        this.consultationDao = consultationDao;
    }
}
