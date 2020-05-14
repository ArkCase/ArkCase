package com.armedia.acm.services.notification.service;

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

import com.armedia.acm.core.AcmApplication;
import com.armedia.acm.core.AcmNotifiableEntity;
import com.armedia.acm.core.AcmObjectType;
import com.armedia.acm.data.AcmNotificationDao;
import com.armedia.acm.data.service.AcmDataService;
import com.armedia.acm.services.labels.service.TranslationService;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConfig;

import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NotificationUtils
{
    private AcmApplication acmAppConfiguration;
    private AcmDataService acmDataService;
    private UserDao userDao;
    private AcmGroupDao groupDao;

    public String buildNotificationLink(String parentType, Long parentId, String relatedObjectType, Long relatedObjectId)
    {
        // If relatedObjectType is null, the parent object is TOP LEVEL (Case File or Complaint)
        // else parent object is nested in top level object
        String linkedObjectType = StringUtils.isEmpty(relatedObjectType) ? parentType : relatedObjectType;
        Long linkedObjectId = StringUtils.isEmpty(relatedObjectType) ? parentId : relatedObjectId;

        // find the object type from the ACM application configuration, and get the URL from the object type
        for (AcmObjectType objectType : getAcmAppConfiguration().getObjectTypes())
        {
            Map<String, String> urlValues = objectType.getUrl();
            if (objectType.getName().equals(parentType) && StringUtils.isNotEmpty(linkedObjectType))
            {
                String objectUrl = urlValues.get(linkedObjectType);
                if (StringUtils.isNotEmpty(objectUrl))
                {
                    objectUrl = String.format(objectUrl, linkedObjectId);
                    return objectUrl;
                }
            }
        }

        return null;
    }

    public String getEmailsCommaSeparatedForParticipants(List<AcmParticipant> participants)
    {
        Set<String> emailAddresses = participants.stream()
                .flatMap(participant -> {
                    if (participant.getParticipantType().equals(NotificationConstants.PARTICIPANT_TYPE_GROUP))
                    {
                        AcmGroup group = getGroupDao().findByName(participant.getParticipantLdapId());
                        if (group != null)
                        {
                            Set<AcmUser> userMembers = group.getUserMembers(true);
                            return userMembers.stream().map(AcmUser::getMail);
                        }
                    }
                    else if (!participant.getReceiverType().equals(NotificationConstants.SPECIAL_PARTICIPANT_TYPE))
                    {
                        AcmUser user = getUserDao().findByUserId(participant.getParticipantLdapId());
                        if (user != null)
                        {
                            return Stream.of(user.getMail());
                        }
                    }
                    return Stream.empty();
                }).collect(Collectors.toSet());
        return String.join(",", emailAddresses);
    }

    public String getEmailForAssignee(String assignee)
    {
        AcmUser user = userDao.findByUserId(assignee);
        return user.getMail();
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public AcmGroupDao getGroupDao()
    {
        return groupDao;
    }

    public void setGroupDao(AcmGroupDao groupDao)
    {
        this.groupDao = groupDao;
    }

    public AcmApplication getAcmAppConfiguration()
    {
        return acmAppConfiguration;
    }

    public void setAcmAppConfiguration(AcmApplication acmAppConfiguration)
    {
        this.acmAppConfiguration = acmAppConfiguration;
    }

    public AcmDataService getAcmDataService()
    {
        return acmDataService;
    }

    public void setAcmDataService(AcmDataService acmDataService)
    {
        this.acmDataService = acmDataService;
    }
}
