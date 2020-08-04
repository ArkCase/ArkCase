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
import com.armedia.acm.core.AcmNotificationReceiver;
import com.armedia.acm.core.AcmObjectType;
import com.armedia.acm.data.AcmNotificationDao;
import com.armedia.acm.data.service.AcmDataService;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserState;
import com.armedia.acm.services.users.model.group.AcmGroup;

import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
        return participants.stream()
                .flatMap(it -> getEmailAddressForParticipant(it.getParticipantType(), it.getParticipantLdapId()).stream())
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.joining(","));
    }

    public String getEmailForUser(String userId)
    {
        AcmUser user = userDao.findByUserId(userId);
        if (user != null && user.getUserState() == AcmUserState.VALID)
        {
            return user.getMail() != null ? user.getMail() : "";
        }
        return "";
    }

    public String getEmailsCommaSeparatedForUsers(List<AcmUser> users)
    {
        return users.stream()
                .filter(it -> it.getUserState() == AcmUserState.VALID)
                .map(AcmUser::getMail)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.joining(","));
    }

    public String getEmailsCommaSeparatedForParticipantsForObject(Long parentObjectId, String parentObjectType)
    {
        AcmNotificationDao notificationDao = acmDataService.getNotificationDaoByObjectType(parentObjectType);
        if (notificationDao != null)
        {
            AcmNotifiableEntity entity = notificationDao.findEntity(parentObjectId);
            if (entity != null)
            {
                Set<AcmNotificationReceiver> participants = entity.getReceivers();
                return participants.stream()
                        .flatMap(it -> getEmailAddressForParticipant(it.getReceiverType(), it.getReceiverLdapId()).stream())
                        .filter(Objects::nonNull)
                        .distinct()
                        .collect(Collectors.joining(","));
            }
        }
        return "";
    }

    public Set<String> getEmailAddressForParticipant(String participantType, String participantLdapId)
    {
        if (participantType.equals(NotificationConstants.PARTICIPANT_TYPE_GROUP))
        {
            AcmGroup group = getGroupDao().findByName(participantLdapId);
            if (group != null)
            {
                Set<AcmUser> members = group.getUserMembers(true);
                return members.stream()
                        .map(AcmUser::getMail)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
            }
        }
        else if (!participantType.equals(NotificationConstants.SPECIAL_PARTICIPANT_TYPE))
        {
            AcmUser user = getUserDao().findByUserId(participantLdapId);
            if (user != null && user.getUserState() == AcmUserState.VALID && user.getMail() != null)
            {
                return new HashSet<>(Collections.singletonList(user.getMail()));
            }
        }
        return new HashSet<>();
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
