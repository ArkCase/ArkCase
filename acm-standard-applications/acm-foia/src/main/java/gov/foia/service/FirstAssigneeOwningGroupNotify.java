package gov.foia.service;

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

import com.armedia.acm.plugins.casefile.model.CaseEvent;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.services.notification.helper.UserInfoHelper;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.notification.service.NotificationService;
import com.armedia.acm.services.notification.service.NotificationUtils;
import com.armedia.acm.services.participants.utils.ParticipantUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;

import java.util.Objects;
import java.util.Set;

public class FirstAssigneeOwningGroupNotify implements ApplicationListener<CaseEvent>
{
    private final transient Logger logger = LogManager.getLogger(getClass());
    private NotificationUtils notificationUtils;
    private UserInfoHelper userInfoHelper;
    private NotificationService notificationService;

    @Override
    public void onApplicationEvent(CaseEvent event)
    {
        if ("com.armedia.acm.casefile.created".equals(event.getEventType().toLowerCase()))
        {
            String assigneeId = ParticipantUtils.getAssigneeIdFromParticipants(event.getCaseFile().getParticipants());
            String owningGroupId = ParticipantUtils.getOwningGroupIdFromParticipants(event.getCaseFile().getParticipants());

            if (Objects.nonNull(owningGroupId))
            {
                logger.debug("On 'Request created' event create notification for members of first assigned owning group [{}].",
                        owningGroupId);

                CaseFile caseFile = event.getCaseFile();

                Set<String> emailAddresses = notificationUtils.getEmailAddressForParticipant(NotificationConstants.PARTICIPANT_TYPE_GROUP,
                        owningGroupId);

                if (Objects.nonNull(assigneeId))
                {
                    String assigneeEmail = notificationUtils.getEmailForUser(assigneeId);
                    if (emailAddresses.contains(assigneeEmail))
                    {
                        emailAddresses.remove(assigneeId);
                    }
                }

                Notification notification = notificationService.getNotificationBuilder()
                        .newNotification("requestAssigned", NotificationConstants.REQUEST_ASSIGNED, event.getObjectType(),
                                event.getObjectId(), assigneeId)
                        .forObjectWithNumber(caseFile.getCaseNumber())
                        .forObjectWithTitle(caseFile.getTitle())
                        .withEmailAddresses(String.join(",", emailAddresses))
                        .withEmailGroup(userInfoHelper.removeGroupPrefix(owningGroupId))
                        .build();

                notificationService.saveNotification(notification);
            }
        }
    }

    public NotificationUtils getNotificationUtils()
    {
        return notificationUtils;
    }

    public void setNotificationUtils(NotificationUtils notificationUtils)
    {
        this.notificationUtils = notificationUtils;
    }

    public UserInfoHelper getUserInfoHelper()
    {
        return userInfoHelper;
    }

    public void setUserInfoHelper(UserInfoHelper userInfoHelper)
    {
        this.userInfoHelper = userInfoHelper;
    }

    public NotificationService getNotificationService()
    {
        return notificationService;
    }

    public void setNotificationService(NotificationService notificationService)
    {
        this.notificationService = notificationService;
    }
}
