package com.armedia.acm.services.notification.web.api;

/**
 * Created by manoj.dhungana on 5/4/2015.
 */

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.services.notification.exception.AcmNotificationException;
import com.armedia.acm.services.notification.model.EmailNotificationDto;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.notification.service.EmailNotificationSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping({"/api/v1/service/notification", "/api/latest/service/notification"})
public class EmailNotificationAPIController {
    private Logger log = LoggerFactory.getLogger(getClass());
    private EmailNotificationSender emailNotificationSender;

    @RequestMapping(value = "/email", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Notification> sendEmail(
            @RequestBody EmailNotificationDto in
    ) throws AcmUserActionFailedException {
        if (log.isInfoEnabled()) {
            log.info("Sending email to recipients");
        }
        List<Notification> notificationList = new ArrayList<>();
        Notification notification = new Notification();
        try {
            if (in == null) {
                throw new AcmNotificationException("Could not create notification for email, missing email addresses and file information");
            }
            notification.setTitle(in.getTitle());
            notification.setNote(in.getNote());
            for (int j = 0; j < in.getEmailAddresses().size(); j++) {
                notification.setUser(in.getEmailAddresses().get(j));
                notification.setUserEmail(in.getEmailAddresses().get(j));
                notificationList.add(getEmailNotificationSender().send(notification));
            }
            return notificationList;
        }
        catch (Exception e) {
            throw new AcmUserActionFailedException("Unable to send emails ", in.getObjectType(), in.getObjectId(), e.getMessage(), e);
        }
    }

    public EmailNotificationSender getEmailNotificationSender() {
        return emailNotificationSender;
    }

    public void setEmailNotificationSender(EmailNotificationSender emailNotificationSender) {
        this.emailNotificationSender = emailNotificationSender;
    }
}