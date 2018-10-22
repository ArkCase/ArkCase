package gov.foia.web.api;

import com.armedia.acm.services.users.model.AcmUser;
import gov.foia.service.NotificationGroupEmailSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping({ "/api/v1/plugin/casefile", "/api/latest/plugin/casefile" })
public class NotificationGroupEmailSenderAPIController
{
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private NotificationGroupEmailSenderService notificationGroupEmailSenderService;

    @RequestMapping(value = "/{caseId}/notification/{notificationGroup}/email", method = RequestMethod.PUT)
    @ResponseBody
    public void sendRequestEmailToNotificationGroup(
            @PathVariable("caseId") Long caseId,
            @PathVariable("notificationGroup") String notificationGroup,
            HttpSession session,
            Authentication authentication) throws Exception
    {
        log.info(String.format("Sending email to notification group [%s]", notificationGroup));
        AcmUser user = (AcmUser) session.getAttribute("acm_user");

        try
        {
            getNotificationGroupEmailSenderService().sendRequestEmailToNotificationGroup(caseId ,notificationGroup, user, authentication);
        }
        catch (Exception e)
        {
            throw new Exception(String.format("Could not send Request Form Document to Notification Group [%s]", notificationGroup), e);
        }
    }

    public NotificationGroupEmailSenderService getNotificationGroupEmailSenderService()
    {
        return notificationGroupEmailSenderService;
    }

    public void setNotificationGroupEmailSenderService(NotificationGroupEmailSenderService notificationGroupEmailSenderService)
    {
        this.notificationGroupEmailSenderService = notificationGroupEmailSenderService;
    }
}
