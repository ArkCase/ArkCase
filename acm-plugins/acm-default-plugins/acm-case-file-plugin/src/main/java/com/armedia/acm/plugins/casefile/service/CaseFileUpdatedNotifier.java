package com.armedia.acm.plugins.casefile.service;

/*-
 * #%L
 * ACM Default Plugin: Case File
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
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.CaseFileModifiedEvent;
import com.armedia.acm.plugins.casefile.model.CaseFileParticipantsModifiedEvent;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.notification.service.NotificationService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;

public class CaseFileUpdatedNotifier implements ApplicationListener<AcmEvent>
{
    private NotificationService notificationService;
    private CaseFileDao caseFileDao;

    private static final Logger logger = LogManager.getLogger(CaseFileUpdatedNotifier.class);

    @Override
    public void onApplicationEvent(AcmEvent event)
    {
        String eventType = event.getEventType();

        if (event instanceof CaseFileModifiedEvent)
        {
            CaseFile caseFile = (CaseFile) event.getSource();

            if (eventType.equals("com.armedia.acm.casefile.status.changed"))
            {
                logger.debug("On 'Case status changed' event create notification for participants.");

                Notification notification = notificationService.getNotificationBuilder()
                        .newNotification("caseStatusChanged", NotificationConstants.CASE_STATUS_CHANGED, caseFile.getObjectType(),
                                caseFile.getId(), event.getUserId())
                        .forObjectWithNumber(caseFile.getCaseNumber())
                        .forObjectWithTitle(caseFile.getTitle())
                        .withEmailAddressesForParticipants(caseFile.getParticipants())
                        .build();

                notificationService.saveNotification(notification);
            }
            else if (eventType.equals("com.armedia.acm.casefile.priority.changed"))
            {
                logger.debug("On 'Case priority changed' event create notification for participants.");

                Notification notification = notificationService.getNotificationBuilder()
                        .newNotification("casePriorityChanged", NotificationConstants.CASE_PRIORITY_CHANGED, caseFile.getObjectType(),
                                caseFile.getId(), event.getUserId())
                        .forObjectWithNumber(caseFile.getCaseNumber())
                        .forObjectWithTitle(caseFile.getTitle())
                        .withEmailAddressesForParticipants(caseFile.getParticipants())
                        .build();

                notificationService.saveNotification(notification);
            }
        }
        else if (event instanceof CaseFileParticipantsModifiedEvent)
        {
            Long caseId = event.getParentObjectId();

            if (eventType.equals("com.armedia.acm.casefile.participant.added"))
            {

                CaseFile caseFile = caseFileDao.find(caseId);
                if (caseFile != null)
                {
                    logger.debug("On 'Case participants added' event create notification for participants.");

                    Notification notification = notificationService.getNotificationBuilder()
                            .newNotification("participantsAdded", NotificationConstants.PARTICIPANTS_ADDED, event.getObjectType(),
                                    event.getObjectId(), event.getUserId())
                            .forRelatedObjectTypeAndId(caseFile.getObjectType(), caseFile.getId())
                            .forRelatedObjectWithNumber(caseFile.getCaseNumber())
                            .withEmailAddressesForParticipants(caseFile.getParticipants())
                            .build();

                    notificationService.saveNotification(notification);
                }

            }
            else if (eventType.equals("com.armedia.acm.casefile.participant.deleted"))
            {
                CaseFile caseFile = caseFileDao.find(caseId);
                if (caseFile != null)
                {
                    logger.debug("On 'Case participants deleted' event create notification for participants.");

                    Notification notification = notificationService.getNotificationBuilder()
                            .newNotification("participantsDeleted", NotificationConstants.PARTICIPANTS_DELETED, event.getObjectType(),
                                    event.getObjectId(), event.getUserId())
                            .forRelatedObjectTypeAndId(caseFile.getObjectType(), caseFile.getId())
                            .forRelatedObjectWithNumber(caseFile.getCaseNumber())
                            .withEmailAddressesForParticipants(caseFile.getParticipants())
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

    public CaseFileDao getCaseFileDao()
    {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }
}
