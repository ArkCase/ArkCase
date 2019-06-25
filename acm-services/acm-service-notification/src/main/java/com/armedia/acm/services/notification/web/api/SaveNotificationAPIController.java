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

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.email.model.EmailMentionsDTO;
import com.armedia.acm.services.email.service.AcmEmailServiceException;
import com.armedia.acm.services.notification.dao.NotificationDao;
import com.armedia.acm.services.notification.model.ApplicationNotificationEvent;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.notification.service.AcmEmailMentionsService;
import com.armedia.acm.services.notification.service.NotificationEventPublisher;

import com.armedia.acm.services.users.model.AcmUser;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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
    private AcmEmailMentionsService acmEmailMentionsService;

    private Logger log = LogManager.getLogger(getClass());

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Notification addNotification(
            @RequestBody Notification notification,
            HttpSession httpSession) throws AcmUserActionFailedException
    {
        if (log.isInfoEnabled())
        {

            if (notification != null)
            {
                log.info("Notification ID : " + notification.getId());
            }
            else
            {
                throw new AcmUserActionFailedException("addNote", NotificationConstants.OBJECT_TYPE, null,
                        "Could not save note, missing parent type and ID", new NullPointerException());
            }
        }

        try
        {
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
            throw new AcmUserActionFailedException("unable to add notification from ", notification.getUser(), notification.getId(),
                    e.getMessage(), e);
        }
    }
    
    @RequestMapping(value = "/mentions", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EmailMentionsDTO createPlainEmail(@RequestBody EmailMentionsDTO in,
                                             Authentication authentication, HttpSession session)
            throws AcmEmailServiceException
    {
        if (null == in)
        {
            throw new AcmEmailServiceException("Could not create email message, invalid input : " + in);
        }

        // the user is stored in the session during login.
        AcmUser user = (AcmUser) session.getAttribute("acm_user");
        acmEmailMentionsService.sendMentionsEmail(in, user.getFullName());

        return in;
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

    public NotificationEventPublisher getNotificationEventPublisher()
    {
        return notificationEventPublisher;
    }

    public void setNotificationEventPublisher(
            NotificationEventPublisher notificationEventPublisher)
    {
        this.notificationEventPublisher = notificationEventPublisher;
    }

    public NotificationDao getNotificationDao()
    {
        return notificationDao;
    }

    public void setNotificationDao(NotificationDao notificationDao)
    {
        this.notificationDao = notificationDao;
    }

    public AcmEmailMentionsService getAcmEmailMentionsService() {
        return acmEmailMentionsService;
    }

    public void setAcmEmailMentionsService(AcmEmailMentionsService acmEmailMentionsService) {
        this.acmEmailMentionsService = acmEmailMentionsService;
    }
}
