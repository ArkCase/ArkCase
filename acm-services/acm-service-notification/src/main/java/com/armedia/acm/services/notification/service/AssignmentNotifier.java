package com.armedia.acm.services.notification.service;

import com.armedia.acm.service.objecthistory.model.AcmAssigneeChangeEvent;
import com.armedia.acm.service.objecthistory.model.AcmAssignment;
import com.armedia.acm.services.notification.model.NotificationConstants;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;

public class AssignmentNotifier implements ApplicationListener<AcmAssigneeChangeEvent>
{
    private NotificationService notificationService;
    private NotificationUtils notificationUtils;

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

            String emailAddress = notificationUtils.getEmailForAssignee(newAssignee);
            notificationService.createNotification("objectAssigned", NotificationConstants.OBJECT_ASSIGNED,
                    assignment.getObjectType(), assignment.getObjectId(), assignment.getObjectName(), assignment.getObjectTitle(),
                    emailAddress, event.getUserId(), newAssignee);

            logger.debug("Notification 'Object assigned' created for object [{}] with id [{}] for assignee [{}] with address [{}].",
                    assignment.getObjectType(), assignment.getObjectId(), newAssignee, emailAddress);
        }

        if (StringUtils.isNotBlank(oldAssignee))
        {
            logger.debug("On 'Assignment changed' event create notification for old assignee [{}].", oldAssignee);

            String emailAddress = notificationUtils.getEmailForAssignee(oldAssignee);
            notificationService.createNotification("objectUnassigned", NotificationConstants.OBJECT_UNASSIGNED,
                    assignment.getObjectType(), assignment.getObjectId(), assignment.getObjectName(), assignment.getObjectTitle(),
                    emailAddress, event.getUserId(), oldAssignee);

            logger.debug("Notification 'Object unassigned' created for object [{}] with id [{}] for assignee [{}] with address [{}].",
                    assignment.getObjectType(), assignment.getId(), oldAssignee, emailAddress);
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
