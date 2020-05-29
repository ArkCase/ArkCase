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

import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.services.notification.dao.NotificationDao;
import com.armedia.acm.services.notification.model.Notification;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.PersistenceException;

import java.util.List;

@Controller
@RequestMapping({ "/api/v1/plugin/notification", "/api/latest/plugin/notification" })
public class ListAllNotificationsAPIController
{
    private NotificationDao notificationDao;

    private final Logger log = LogManager.getLogger(getClass());

    @RequestMapping(value = "/{user:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Notification> findAllNotificationsInParentObject(@PathVariable("user") String user)
            throws AcmListObjectsFailedException
    {
        log.info("Find notifications for user [{}].", user);
        try
        {
            List<Notification> notificationList = getNotificationDao().listNotifications(user);
            log.debug("Found [{}] notifications for user [{}].", notificationList.size(), user);
            return notificationList;
        }
        catch (PersistenceException e)
        {
            throw new AcmListObjectsFailedException("p", e.getMessage(), e);
        }
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
