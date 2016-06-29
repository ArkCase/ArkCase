package com.armedia.acm.services.notification.web.api;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.notification.exception.AcmNotificationException;
import com.armedia.acm.services.notification.model.EmailNotificationDto;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.service.NotificationSender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping({ "/api/v1/service/notification", "/api/latest/service/notification" })
public class EmailNotificationAPIController
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private NotificationSender notificationSender;

    @RequestMapping(value = "/email", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Notification> sendEmail(@RequestBody List<EmailNotificationDto> in, Authentication authentication)
            throws AcmUserActionFailedException
    {
        if (log.isInfoEnabled())
        {
            log.info("Sending email to recipients");
        }
        try
        {
            if (in == null)
            {
                throw new AcmNotificationException("Could not create notification for email, missing email addresses and file information");
            }
            return getNotificationSender().sendEmailNotificationWithLinks(in, authentication);
        } catch (Exception e)
        {
            throw new AcmUserActionFailedException("Unable to send emails ", null, null, e.getMessage(), e);
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