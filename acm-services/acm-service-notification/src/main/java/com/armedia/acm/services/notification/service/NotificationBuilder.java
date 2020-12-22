package com.armedia.acm.services.notification.service;

/*-
 * #%L
 * ACM Service: Notification
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.users.model.AcmUser;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class NotificationBuilder
{
    private NotificationUtils notificationUtils;
    private NotificationFormatter notificationFormatter;

    public Builder newNotification(String template, String title, String objectType, Long objectId, String userId)
    {
        return new Builder(template, title, objectType, objectId, userId);
    }

    public class Builder
    {
        private final Notification notification;

        private Builder(String template, String title, String objectType, Long objectId, String userId)
        {
            this.notification = new Notification();
            this.notification.setTitle(title);
            this.notification.setTemplateModelName(template);
            this.notification.setParentType(objectType);
            this.notification.setParentId(objectId);
            this.notification.setUser(userId);
            this.notification.setStatus(NotificationConstants.STATUS_NEW);
            this.notification.setData(
                    String.format("{\"usr\":\"/plugin/%s/%s\"}", notification.getParentType().toLowerCase(), notification.getParentId()));
            this.notification.setActionDate(new Date());
            this.notification.setType("user");
        }

        public Builder builder(String template, String title, String objectType, Long objectId,
                String userId)
        {
            return new Builder(template, title, objectType, objectId, userId);
        }

        public Notification build(String userInTitle)
        {
            if (notification.getRelatedObjectId() != null && notification.getRelatedObjectType() != null)
            {
                notification.setTitle(notificationFormatter.buildTitle(notification.getTitle(), notification.getRelatedObjectNumber(),
                        notification.getRelatedObjectType(), userInTitle));
            }
            else
            {
                notification.setTitle(notificationFormatter.buildTitle(notification.getTitle(), notification.getParentName(),
                        notification.getParentType(), userInTitle));
            }
            return notification;
        }

        public Notification build()
        {
            if (notification.getRelatedObjectId() != null && notification.getRelatedObjectType() != null)
            {
                notification.setTitle(notificationFormatter.buildTitle(notification.getTitle(), notification.getRelatedObjectNumber(),
                        notification.getRelatedObjectType(), null));
            }
            else
            {
                notification.setTitle(notificationFormatter.buildTitle(notification.getTitle(), notification.getParentName(),
                        notification.getParentType(), null));
            }
            return notification;
        }

        public Builder withEmailAddresses(String emailAddressesCommaSeparated)
        {
            this.notification.setEmailAddresses(emailAddressesCommaSeparated);
            return this;
        }

        public Builder withEmailAddressesForUsers(List<AcmUser> users)
        {
            String emailAddressesCommaSeparated = notificationUtils.getEmailsCommaSeparatedForUsers(users);
            this.notification.setEmailAddresses(emailAddressesCommaSeparated);
            return this;
        }

        public Builder withEmailAddressForUser(String userId)
        {
            String emailAddress = notificationUtils.getEmailForUser(userId);
            this.notification.setEmailAddresses(emailAddress);
            return this;
        }

        public Builder withEmailAddressesForParticipants(List<AcmParticipant> participants)
        {
            String emailAddress = notificationUtils.getEmailsCommaSeparatedForParticipants(participants);
            this.notification.setEmailAddresses(emailAddress);
            return this;
        }

        public Builder withEmailAddressesForParticipant(String participantType, String participantLdapId)
        {
            Set<String> emailAddresses = notificationUtils.getEmailAddressForParticipant(participantType, participantLdapId);
            this.notification.setEmailAddresses(String.join(",", emailAddresses));
            return this;
        }

        public Builder withEmailAddressesForParticipantsForObject(String objectType, Long objectId)
        {
            String emailAddress = notificationUtils.getEmailsCommaSeparatedForParticipantsForObject(objectId, objectType);
            this.notification.setEmailAddresses(emailAddress);
            return this;
        }

        public Builder withEmailGroup(String group)
        {
            this.notification.setEmailGroup(group);
            return this;
        }

        public Builder withNote(String note)
        {
            this.notification.setNote(note);
            return this;
        }

        public Builder withData(String data)
        {
            this.notification.setData(data);
            return this;
        }

        public Builder withFiles(List<EcmFileVersion> files)
        {
            this.notification.setAttachFiles(true);
            this.notification.setFiles(files);
            return this;
        }

        public Builder forObjectWithNumber(String objectNumber)
        {
            this.notification.setParentName(objectNumber);
            return this;
        }

        public Builder forObjectWithTitle(String objectTitle)
        {
            this.notification.setParentTitle(objectTitle);
            return this;
        }

        public Builder forRelatedObjectTypeAndId(String objectType, Long objectId)
        {
            this.notification.setRelatedObjectType(objectType);
            this.notification.setRelatedObjectId(objectId);
            return this;
        }

        public Builder forRelatedObjectWithNumber(String relatedObjectNumber)
        {
            this.notification.setRelatedObjectNumber(relatedObjectNumber);
            return this;
        }

        public Builder withSubject(String subject)
        {
            this.notification.setSubject(subject);
            return this;
        }
        
        public Builder withNotificationType(String notificationType)
        {
            this.notification.setNotificationType(notificationType);
            return this;
        }
    }

    public void setNotificationUtils(NotificationUtils notificationUtils)
    {
        this.notificationUtils = notificationUtils;
    }

    public void setNotificationFormatter(NotificationFormatter notificationFormatter)
    {
        this.notificationFormatter = notificationFormatter;
    }
}
