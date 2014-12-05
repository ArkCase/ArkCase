package com.armedia.acm.services.notification.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.notification.dao.NotificationDao;
import org.activiti.engine.impl.util.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.PersistenceException;


@Controller
@RequestMapping({ "/api/v1/plugin/notification", "/api/latest/plugin/notification" })
public class DeleteNotificationByIdAPIController {

    private NotificationDao notificationDao;
    //MediaType.APPLICATION_JSON_VALUE
    private Logger log = LoggerFactory.getLogger(getClass());
    @RequestMapping(value = "/{notificationId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteNotificationById(
            @PathVariable("notificationId") Long id

    ) throws AcmObjectNotFoundException, AcmUserActionFailedException {
        if (log.isInfoEnabled()) {
            log.info("Finding notification with ID: " + id);
        }
        if(id != null){
            try
            {
                JSONObject objectToReturnJSON = new JSONObject();
                getNotificationDao().deleteNotificationById(id);
                log.info("Deleting notification by id '" + id + "'");
                log.debug("Notification ID : " + id);

                objectToReturnJSON.put("deletedNotificationId", id);

                String objectToReturn;
                objectToReturn = objectToReturnJSON.toString();

                return objectToReturn;
            }
            catch (PersistenceException e)
            {
                throw new AcmUserActionFailedException("Delete", "notification", id, e.getMessage(), e);
            }
        }
        throw new AcmObjectNotFoundException ("Could not find notification", id, "", null);
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




