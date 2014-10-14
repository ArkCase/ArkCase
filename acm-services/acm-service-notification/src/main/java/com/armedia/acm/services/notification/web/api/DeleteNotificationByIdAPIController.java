package com.armedia.acm.services.notification.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.notification.dao.NotificationDao;
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

/**
 * Created by manojd on 8/16/14.
 */
@Controller
@RequestMapping({ "/api/v1/plugin/notification", "/api/latest/plugin/notification" })
public class DeleteNotificationByIdAPIController {

    private NotificationDao notificationDao;
    private NotificationEventPublisher notificationEventPublisher;

    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/delete/{user}/{notificationId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteNotificationById(
            @PathVariable("user") String user,
            @PathVariable("notificationId") Long id
    ) throws AcmObjectNotFoundException, AcmUserActionFailedException {
        if (log.isInfoEnabled()) {
            log.info("Deleting notification with ID: " + id);
        }
        if(user != null && id != null){
            try
            {
                getNotificationDao().deleteNotificationById(id);
                log.debug("Notification : " + id + " deleted for user : " + user);
            }
            catch (PersistenceException e)
            {
                throw new AcmUserActionFailedException("Delete", "notification", id, e.getMessage(), e);
            }
        }
        throw new AcmObjectNotFoundException ("Could not find", id, "notification with this id", null);
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




