package com.armedia.acm.services.note.service;

import com.armedia.acm.services.note.model.ApplicationNoteEvent;
import com.armedia.acm.services.note.model.Note;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.notification.service.NotificationService;
import com.armedia.acm.services.notification.service.NotificationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;

public class NoteAddedNotifier implements ApplicationListener<ApplicationNoteEvent>
{
    private NotificationService notificationService;
    private NotificationUtils notificationUtils;

    private static final Logger logger = LogManager.getLogger(NoteAddedNotifier.class);

    @Override
    public void onApplicationEvent(ApplicationNoteEvent event)
    {
        String eventType = event.getEventType();
        if (eventType.equals("com.armedia.acm.app.note.added"))
        {
            Note note = (Note) event.getSource();
            logger.debug("On 'Note added' event create notification for participants.");

            String emailAddresses = notificationUtils.getEmailsCommaSeparatedForParticipantsForObject(note.getParentId(), note.getParentType());
            notificationService.createNotification("noteAdded", NotificationConstants.NOTE_ADDED, note.getObjectType(),
                    note.getId(), null, null, note.getParentObjectId(), note.getParentObjectType(), note.getParentTitle(),
                    emailAddresses, event.getUserId(), null, null);

            logger.debug("Notification 'Note added' created for note [{}] for participants with addresses [{}].",
                    note.getId(), emailAddresses);
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

    public NotificationUtils getNotificationUtils()
    {
        return notificationUtils;
    }

    public void setNotificationUtils(NotificationUtils notificationUtils)
    {
        this.notificationUtils = notificationUtils;
    }
}
