package com.armedia.acm.plugins.complaint.service;

import com.armedia.acm.core.model.AcmEvent;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.ComplaintModifiedEvent;
import com.armedia.acm.plugins.complaint.model.ComplaintParticipantsModifiedEvent;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.notification.service.NotificationService;
import com.armedia.acm.services.notification.service.NotificationUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;

public class ComplaintUpdatedNotifier implements ApplicationListener<AcmEvent>
{
    private NotificationService notificationService;
    private NotificationUtils notificationUtils;
    private ComplaintDao complaintDao;

    private static final Logger logger = LogManager.getLogger(ComplaintUpdatedNotifier.class);

    @Override
    public void onApplicationEvent(AcmEvent event)
    {
        String eventType = event.getEventType();

        if (event instanceof ComplaintModifiedEvent)
        {
            Complaint complaint = (Complaint) event.getSource();

            String emailAddresses = notificationUtils.getEmailsCommaSeparatedForParticipants(complaint.getParticipants());

            if (eventType.equals("com.armedia.acm.complaint.status.changed"))
            {
                logger.debug("On 'Complaint status changed' event create notification for participants.");

                notificationService.createNotification("complaintStatusChanged", NotificationConstants.COMPLAINT_STATUS_CHANGED,
                        complaint.getObjectType(), complaint.getId(), complaint.getComplaintNumber(), complaint.getComplaintTitle(),
                        emailAddresses, event.getUserId());

                logger.debug("Notification 'Complaint status changed' created for complaint [{}] for participants with addresses [{}].",
                        complaint.getId(), emailAddresses);
            }
            else if (eventType.equals("com.armedia.acm.complaint.priority.changed"))
            {
                logger.debug("On 'Complaint priority changed' event create notification for participants.");

                notificationService.createNotification("complaintPriorityChanged", NotificationConstants.COMPLAINT_PRIORITY_CHANGED,
                        complaint.getObjectType(), complaint.getId(), complaint.getComplaintNumber(), complaint.getTitle(), emailAddresses,
                        event.getUserId());

                logger.debug("Notification 'Complaint priority changed' created for complaint [{}] for participants with addresses [{}].",
                        complaint.getId(), emailAddresses);
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

                    String emailAddresses = notificationUtils.getEmailsCommaSeparatedForParticipants(complaint.getParticipants());
                    notificationService.createNotification("participantsAdded", NotificationConstants.PARTICIPANTS_ADDED,
                            event.getObjectType(), event.getObjectId(), null, null, complaint.getId(),
                            complaint.getObjectType(), complaint.getComplaintNumber(), emailAddresses, event.getUserId(), null, null);

                    logger.debug("Notification 'Participants added' created for complaint [{}] for participants with addresses [{}].",
                            complaint.getId(), emailAddresses);
                }
            }
            else if (eventType.equals("com.armedia.acm.complaint.participant.deleted"))
            {
                Complaint complaint = complaintDao.find(complaintId);
                if (complaint != null)
                {
                   logger.debug("On 'Complaint participants deleted' event create notification for participants.");

                    String emailAddresses = notificationUtils.getEmailsCommaSeparatedForParticipants(complaint.getParticipants());
                    notificationService.createNotification("participantsDeleted", NotificationConstants.PARTICIPANTS_DELETED,
                            event.getObjectType(), event.getObjectId(), null, null, complaint.getId(),
                            complaint.getObjectType(), complaint.getComplaintNumber(), emailAddresses, event.getUserId(), null, null);

                    logger.debug("Notification 'Participants deleted' created for complaint [{}] for participants with addresses [{}].",
                            complaint.getId(), emailAddresses);
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

    public NotificationUtils getNotificationUtils()
    {
        return notificationUtils;
    }

    public void setNotificationUtils(NotificationUtils notificationUtils)
    {
        this.notificationUtils = notificationUtils;
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
