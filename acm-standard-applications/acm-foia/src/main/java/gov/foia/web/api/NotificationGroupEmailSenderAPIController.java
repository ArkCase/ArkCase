package gov.foia.web.api;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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

import com.armedia.acm.services.users.model.AcmUser;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import gov.foia.service.NotificationGroupEmailSenderService;

@Controller
@RequestMapping({ "/api/v1/plugin/casefile", "/api/latest/plugin/casefile" })
public class NotificationGroupEmailSenderAPIController
{
    private final Logger log = LogManager.getLogger(this.getClass());
    private NotificationGroupEmailSenderService notificationGroupEmailSenderService;

    @RequestMapping(value = "/{caseId}/notification/{notificationGroup}/email", method = RequestMethod.PUT)
    @ResponseBody
    public void sendRequestEmailToNotificationGroup(
            @PathVariable("caseId") Long caseId,
            @PathVariable("notificationGroup") String notificationGroup,
            HttpSession session,
            Authentication authentication) throws Exception
    {
        log.info(String.format("Sending email to notification group [%s]", notificationGroup));
        AcmUser user = (AcmUser) session.getAttribute("acm_user");

        try
        {
            getNotificationGroupEmailSenderService().sendRequestEmailToNotificationGroup(caseId ,notificationGroup, user, authentication);
        }
        catch (Exception e)
        {
            throw new Exception(String.format("Could not send Request Form Document to Notification Group [%s]", notificationGroup), e);
        }
    }

    public NotificationGroupEmailSenderService getNotificationGroupEmailSenderService()
    {
        return notificationGroupEmailSenderService;
    }

    public void setNotificationGroupEmailSenderService(NotificationGroupEmailSenderService notificationGroupEmailSenderService)
    {
        this.notificationGroupEmailSenderService = notificationGroupEmailSenderService;
    }
}
