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
import com.armedia.acm.services.email.model.EmailWithEmbeddedLinksDTO;
import com.armedia.acm.services.email.model.EmailWithEmbeddedLinksResultDTO;
import com.armedia.acm.services.notification.exception.AcmNotificationException;
import com.armedia.acm.services.notification.service.NotificationSenderFactory;
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
    private NotificationSenderFactory notificationSenderFactory;

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
            return getNotificationSenderFactory().getNotificationSender().sendEmailWithEmbeddedLinks(in, authentication, user);
        }
        catch (Exception e)
        {
            throw new AcmUserActionFailedException(
                    "Could not send emails with embedded links, among other things check your request body. Exception message is : ", null,
                    null, e.getMessage(), e);
        }

    }

    public NotificationSenderFactory getNotificationSenderFactory()
    {
        return notificationSenderFactory;
    }

    public void setNotificationSenderFactory(NotificationSenderFactory notificationSenderFactory)
    {
        this.notificationSenderFactory = notificationSenderFactory;
    }

}
