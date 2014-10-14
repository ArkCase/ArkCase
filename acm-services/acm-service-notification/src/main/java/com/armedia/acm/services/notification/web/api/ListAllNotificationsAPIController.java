package com.armedia.acm.services.notification.web.api;

import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.notification.dao.NotificationDao;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.service.NotificationEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.PersistenceException;
import java.util.List;

/**
 * Created by manojd on 8/16/14.
 */
@Controller
@RequestMapping({ "/api/v1/plugin/notification", "/api/latest/plugin/notification" })
public class ListAllNotificationsAPIController {

    private NotificationDao notificationDao;
    private NotificationEventPublisher notificationEventPublisher;

    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/list/{user}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Notification> findAllNotifications(
            @PathVariable("user") String user
    ) throws AcmObjectNotFoundException, AcmUserActionFailedException, AcmListObjectsFailedException {
        if (log.isInfoEnabled()) {
            log.info("Finding all notifications");
        }
        if(user != null){
            try {
                List<Notification> notificationList = getNotificationDao().listNotifications();
                log.debug("notificationList size " + notificationList.size());
                return notificationList;
            } catch (PersistenceException e) {
                throw new AcmListObjectsFailedException("p", e.getMessage(), e);
            }
        }
        throw new AcmListObjectsFailedException("wrong input", "user: ", null);
    }

    public NotificationDao getNotificationDao()
    {
        return notificationDao;
    }

    public void setNotificationDao(NotificationDao notificationDao)
    {
        this.notificationDao = notificationDao;
    }
}