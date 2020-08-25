package com.armedia.acm.services.note.service;

/*-
 * #%L
 * ACM Service: Note
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

import com.armedia.acm.services.note.model.ApplicationNoteEvent;
import com.armedia.acm.services.note.model.Note;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.notification.service.NotificationService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;

public class NoteAddedNotifier implements ApplicationListener<ApplicationNoteEvent>
{
    private NotificationService notificationService;

    private static final Logger logger = LogManager.getLogger(NoteAddedNotifier.class);

    @Override
    public void onApplicationEvent(ApplicationNoteEvent event)
    {
        String eventType = event.getEventType();
        if (eventType.equals("com.armedia.acm.app.note.added"))
        {
            logger.debug("On 'Note added' event create notification for participants.");

            Note note = (Note) event.getSource();

            Notification notification = notificationService.getNotificationBuilder()
                    .newNotification("noteAdded", NotificationConstants.NOTE_ADDED, note.getObjectType(), note.getId(), event.getUserId())
                    .forRelatedObjectTypeAndId(note.getParentObjectType(), note.getParentObjectId())
                    .forRelatedObjectWithNumber(note.getParentTitle())
                    .withEmailAddressesForParticipantsForObject(note.getParentType(), note.getParentId())
                    .build();

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
