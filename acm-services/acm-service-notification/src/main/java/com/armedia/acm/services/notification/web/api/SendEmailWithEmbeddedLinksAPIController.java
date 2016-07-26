package com.armedia.acm.services.notification.web.api;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.service.outlook.model.EmailWithEmbeddedLinksDTO;
import com.armedia.acm.service.outlook.model.EmailWithEmbeddedLinksResultDTO;
import com.armedia.acm.services.notification.exception.AcmNotificationException;
import com.armedia.acm.services.notification.service.NotificationSender;
import com.armedia.acm.services.users.model.AcmUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import java.util.List;

@Controller
@RequestMapping({ "/api/v1/service/notification/email", "/api/latest/service/notification/email" })
public class SendEmailWithEmbeddedLinksAPIController
{

    private Logger log = LoggerFactory.getLogger(getClass());
    private NotificationSender notificationSender;

    @RequestMapping(value = "/withembeddedlinks", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<EmailWithEmbeddedLinksResultDTO> createEmailWithEmbeddedLinks(@RequestBody EmailWithEmbeddedLinksDTO in,
            Authentication authentication, HttpSession session) throws AcmNotificationException, AcmUserActionFailedException
    {

        if (null == in)
        {
            throw new AcmNotificationException("Could not create email message, invalid input : " + in);
        }
        // the user is stored in the session during login.
        AcmUser user = (AcmUser) session.getAttribute("acm_user");

        try
        {
            return getNotificationSender().sendEmailWithEmbeddedLinks(in, authentication, user);
        } catch (Exception e)
        {
            throw new AcmUserActionFailedException(
                    "Could not send emails with embedded links, among other things check your request body. Exception message is : ", null,
                    null, e.getMessage(), e);
        }

    }

    public NotificationSender getNotificationSender()
    {
        return notificationSender;
    }

    public void setNotificationSender(NotificationSender notificationSender)
    {
        this.notificationSender = notificationSender;
    }

}
