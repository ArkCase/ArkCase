package gov.foia.listener;

import com.armedia.acm.service.objecthistory.model.AcmAssigneeChangeEvent;
import com.armedia.acm.service.objecthistory.model.AcmAssignment;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.notification.service.AssignmentNotifier;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FoiaAssignmentNotifier extends AssignmentNotifier
{
    private static final Logger logger = LogManager.getLogger(FoiaAssignmentNotifier.class);

    @Override
    public void onApplicationEvent(AcmAssigneeChangeEvent event)
    {
        AcmAssignment assignment = (AcmAssignment) event.getSource();

        String newAssignee = assignment.getNewAssignee();
        String oldAssignee = assignment.getOldAssignee();

        if (StringUtils.isNotBlank(newAssignee) && !newAssignee.equals("None"))
        {
            logger.debug("On 'Assignment changed' event create notification for new assignee [{}].", newAssignee);
            String emailAddress = getNotificationUtils().getEmailForUser(newAssignee);
            if (assignment.getObjectType().equals("CASE_FILE"))
            {
                getNotificationService().createNotification("requestAssignedAssignee", NotificationConstants.OBJECT_ASSIGNED,
                        assignment.getObjectType(), assignment.getObjectId(), assignment.getObjectName(), assignment.getObjectTitle(),
                        emailAddress, event.getUserId(), newAssignee);
            }
            else if (assignment.getObjectType().equals("TASK"))
            {
                getNotificationService().createNotification("taskAssignedAssignee", NotificationConstants.OBJECT_ASSIGNED,
                        assignment.getObjectType(), assignment.getObjectId(), assignment.getObjectName(), assignment.getObjectTitle(),
                        emailAddress, event.getUserId(), newAssignee);
            }
            else
            {
                getNotificationService().createNotification("objectAssigned", NotificationConstants.OBJECT_ASSIGNED,
                        assignment.getObjectType(), assignment.getObjectId(), assignment.getObjectName(), assignment.getObjectTitle(),
                        emailAddress, event.getUserId(), newAssignee);
            }

            logger.debug("Notification 'Object assigned' created for object [{}] with id [{}] for assignee [{}] with address [{}].",
                    assignment.getObjectType(), assignment.getObjectId(), newAssignee, emailAddress);
        }

        if (StringUtils.isNotBlank(oldAssignee))
        {
            logger.debug("On 'Assignment changed' event create notification for old assignee [{}].", oldAssignee);

            String emailAddress = getNotificationUtils().getEmailForUser(oldAssignee);
            if (assignment.getObjectType().equals("CASE_FILE"))
            {
                getNotificationService().createNotification("requestUnassignedAssignee", NotificationConstants.OBJECT_UNASSIGNED,
                        assignment.getObjectType(), assignment.getObjectId(), assignment.getObjectName(), assignment.getObjectTitle(),
                        emailAddress, event.getUserId(), oldAssignee);
            }
            else if (assignment.getObjectType().equals("TASK"))
            {
                getNotificationService().createNotification("taskUnassignedAssignee", NotificationConstants.OBJECT_UNASSIGNED,
                        assignment.getObjectType(), assignment.getObjectId(), assignment.getObjectName(), assignment.getObjectTitle(),
                        emailAddress, event.getUserId(), oldAssignee);
            }
            else
            {
                getNotificationService().createNotification("objectUnassigned", NotificationConstants.OBJECT_UNASSIGNED,
                        assignment.getObjectType(), assignment.getObjectId(), assignment.getObjectName(), assignment.getObjectTitle(),
                        emailAddress, event.getUserId(), oldAssignee);
            }

            logger.debug("Notification 'Object unassigned' created for object [{}] with id [{}] for assignee [{}] with address [{}].",
                    assignment.getObjectType(), assignment.getId(), oldAssignee, emailAddress);
        }
    }
}
