package com.armedia.acm.services.notification.web.api;

/*-
 * #%L
 * ACM Service: Notification
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.notification.dao.NotificationDao;

import org.activiti.engine.impl.util.json.JSONObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.PersistenceException;

@Controller
@RequestMapping({ "/api/v1/plugin/notification", "/api/latest/plugin/notification" })
public class DeleteNotificationByIdAPIController
{

    private NotificationDao notificationDao;
    // MediaType.APPLICATION_JSON_VALUE
    private Logger log = LogManager.getLogger(getClass());

    @RequestMapping(value = "/{notificationId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteNotificationById(
            @PathVariable("notificationId") Long id

    ) throws AcmObjectNotFoundException, AcmUserActionFailedException
    {
        if (log.isInfoEnabled())
        {
            log.info("Finding notification with ID: " + id);
        }
        if (id != null)
        {
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
        throw new AcmObjectNotFoundException("Could not find notification", id, "", null);
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
