package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.core.model.AcmEvent;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.CaseFileModifiedEvent;
import com.armedia.acm.plugins.casefile.model.CaseFileParticipantsModifiedEvent;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.notification.service.NotificationService;
import com.armedia.acm.services.notification.service.NotificationUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;

public class CaseFileUpdatedNotifier implements ApplicationListener<AcmEvent>
{
    private NotificationService notificationService;
    private NotificationUtils notificationUtils;
    private CaseFileDao caseFileDao;

    private static final Logger logger = LogManager.getLogger(CaseFileUpdatedNotifier.class);

    @Override
    public void onApplicationEvent(AcmEvent event)
    {
        String eventType = event.getEventType();

        if (event instanceof CaseFileModifiedEvent)
        {
            CaseFile caseFile = (CaseFile) event.getSource();
            String emailAddresses = notificationUtils.getEmailsCommaSeparatedForParticipants(caseFile.getParticipants());

            if (eventType.equals("com.armedia.acm.casefile.status.changed"))
            {
                logger.debug("On 'Case status changed' event create notification for participants.");

                notificationService.createNotification("caseStatusChanged", NotificationConstants.CASE_STATUS_CHANGED,
                        caseFile.getObjectType(), caseFile.getId(), caseFile.getCaseNumber(), caseFile.getTitle(), emailAddresses,
                        event.getUserId(), null);

                logger.debug("Notification 'Case status changed' created for case [{}] for participants with addresses [{}].",
                        caseFile.getId(), emailAddresses);
            }
            else if (eventType.equals("com.armedia.acm.casefile.priority.changed"))
            {
                logger.debug("On 'Case priority changed' event create notification for participants.");

                notificationService.createNotification("casePriorityChanged", NotificationConstants.CASE_PRIORITY_CHANGED,
                        caseFile.getObjectType(), caseFile.getId(), caseFile.getCaseNumber(), caseFile.getTitle(), emailAddresses,
                        event.getUserId(), null);

                logger.debug("Notification 'Case priority changed' created for case [{}] for participants with addresses [{}].",
                        caseFile.getId(), emailAddresses);
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

                    String emailAddresses = notificationUtils.getEmailsCommaSeparatedForParticipants(caseFile.getParticipants());
                    notificationService.createNotification("participantsAdded", NotificationConstants.PARTICIPANTS_ADDED,
                            event.getObjectType(), event.getObjectId(), null, null, caseFile.getId(),
                            caseFile.getObjectType(), caseFile.getCaseNumber(), emailAddresses, event.getUserId(), null, null);

                    logger.debug("Notification 'Participants added' created for Case [{}] for participants with addresses [{}].",
                            caseFile.getId(), emailAddresses);
                }

            }
            else if (eventType.equals("com.armedia.acm.casefile.participant.deleted"))
            {
                CaseFile caseFile = caseFileDao.find(caseId);
                if (caseFile != null)
                {
                    logger.debug("On 'Case participants deleted' event create notification for participants.");

                    String emailAddresses = notificationUtils.getEmailsCommaSeparatedForParticipants(caseFile.getParticipants());
                    notificationService.createNotification("participantsDeleted", NotificationConstants.PARTICIPANTS_DELETED,
                            event.getObjectType(), event.getObjectId(), null, null, caseFile.getId(),
                            caseFile.getObjectType(), caseFile.getCaseNumber(), emailAddresses, event.getUserId(), null, null);

                    logger.debug("Notification 'Participants deleted' created for Case [{}] for participants with addresses [{}].",
                            caseFile.getId(), emailAddresses);

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

    public CaseFileDao getCaseFileDao()
    {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }
}
