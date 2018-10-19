package gov.foia.service;

import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import gov.foia.model.FOIARequest;

/**
 * Service for notifications to the executive group.
 * 
 * @author bojan.milenkoski
 */
public class FOIAExecutiveGroupNotificationService
{
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private CaseFileDao caseFileDao;
    private NotificationGroupEmailSenderService notificationGroupEmailSenderService;
    private UserDao userDao;

    public void sendFulfillEmailNotification(Long requestId) throws Exception
    {
        FOIARequest request = (FOIARequest) caseFileDao.find(requestId);
        String notificationGroup = request.getNotificationGroup();

        if (request.getQueue().getName().equals("Fulfill") && request.getPreviousQueue().getName().equals("Intake")
                && !StringUtils.isEmpty(notificationGroup))
        {

            log.info(String.format("Sending email to notification group [%s]", notificationGroup));
            AcmUser user = userDao.findByUserId(request.getModifier());

            try
            {
                getNotificationGroupEmailSenderService().sendRequestEmailToNotificationGroup(requestId, notificationGroup, user,
                        SecurityContextHolder.getContext().getAuthentication());
            }
            catch (Exception e)
            {
                throw new Exception(String.format("Could not send Request Form Document to Notification Group [%s]", notificationGroup), e);
            }
        }
    }

    public CaseFileDao getCaseFileDao()
    {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }

    public NotificationGroupEmailSenderService getNotificationGroupEmailSenderService()
    {
        return notificationGroupEmailSenderService;
    }

    public void setNotificationGroupEmailSenderService(NotificationGroupEmailSenderService notificationGroupEmailSenderService)
    {
        this.notificationGroupEmailSenderService = notificationGroupEmailSenderService;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }
}
