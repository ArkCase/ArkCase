package com.armedia.acm.services.notification.web.api;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.notification.dao.NotificationDao;
import com.armedia.acm.services.notification.exception.AcmNotificationException;
import com.armedia.acm.services.notification.model.ApplicationNotificationEvent;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.service.NotificationEventPublisher;
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

@Controller
@RequestMapping({ "/api/v1/plugin/notification", "/api/latest/plugin/notification" })
public class SaveNotificationAPIController
{


    private NotificationDao notificationDao;
    private NotificationEventPublisher notificationEventPublisher;

    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Notification addNotification(
            @RequestBody Notification notification,
            HttpSession httpSession
    ) throws AcmUserActionFailedException
    {
        if ( log.isInfoEnabled() )
        {

            if(notification != null){
                log.info("Notification ID : " + notification.getId());
            }
        }

        try
        {
            if(notification == null)
            {
                throw new AcmNotificationException("Could not save notification, missing parent type and ID");
            }

            // to db
            Notification newNotification = new Notification();

            newNotification.setId(notification.getId());
            newNotification.setCreated(notification.getCreated());
            newNotification.setCreator(notification.getCreator());
            newNotification.setStatus(notification.getStatus());
            newNotification.setAction(notification.getAction());
            newNotification.setNote(notification.getNote());
            newNotification.setModifier(notification.getModifier());
            newNotification.setModified(notification.getModified());
            newNotification.setData(notification.getData());
            newNotification.setType(notification.getType());
            newNotification.setUser(notification.getUser());


            Notification savedNotification = getNotificationDao().save(notification);

            publishNotificationEvent(httpSession, savedNotification, true);

            return savedNotification;
        }
        catch (Exception e)
        {
            // gen up a fake notification so we can audit the failure
            Notification fakeNotification = new Notification();
            fakeNotification.setId(notification.getId());
            log.info("fake id : " + fakeNotification.getId());
            log.info("fake id 2: " + notification.getId());

            fakeNotification.setId(notification.getId());
            fakeNotification.setCreated(notification.getCreated());
            fakeNotification.setCreator(notification.getCreator());
            fakeNotification.setStatus(notification.getStatus());
            fakeNotification.setAction(notification.getAction());
            fakeNotification.setNote(notification.getNote());
            fakeNotification.setModifier(notification.getModifier());
            fakeNotification.setModified(notification.getModified());
            fakeNotification.setData(notification.getData());
            fakeNotification.setType(notification.getType());
            fakeNotification.setUser(notification.getUser());

            publishNotificationEvent(httpSession, fakeNotification, false);
            throw new AcmUserActionFailedException("unable to add notification from ", notification.getUser(), notification.getId(), e.getMessage(), e);
        }
    }

    protected void publishNotificationEvent(
            HttpSession httpSession,
            Notification notification,
            boolean succeeded)
    {
        String ipAddress = (String) httpSession.getAttribute("acm_ip_address");
        ApplicationNotificationEvent event = new ApplicationNotificationEvent(notification, "notification", succeeded, ipAddress);
        getNotificationEventPublisher().publishNotificationEvent(event);
    }

    public NotificationEventPublisher getNotificationEventPublisher() {
        return notificationEventPublisher;
    }

    public void setNotificationEventPublisher(
            NotificationEventPublisher notificationEventPublisher) {
        this.notificationEventPublisher = notificationEventPublisher;
    }
    public NotificationDao getNotificationDao() {
        return notificationDao;
    }

    public void setNotificationDao(NotificationDao notificationDao) {
        this.notificationDao = notificationDao;
    }

}

