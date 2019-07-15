package com.armedia.acm.services.notification.service.provider;

/*-
 * #%L
 * ACM Service: Notification
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.provider.TemplateModelProvider;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.data.service.AcmDataService;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.service.provider.model.GenericTemplateModel;
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.spring.SpringContextHolder;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Objects;

public class NotificationGroupTemplateModelProvider implements TemplateModelProvider<GenericTemplateModel>
{
    private AcmDataService dataService;
    private UserDao userDao;
    private SpringContextHolder contextHolder;

    private final Logger log = LoggerFactory.getLogger(getClass().getName());


    @Override
    public GenericTemplateModel getModel(Object notificationObject)
    {
        GenericTemplateModel genericTemplateModelData = new GenericTemplateModel();
        Notification notification = (Notification) notificationObject;
        genericTemplateModelData.setObjectNumber(notification.getParentId().toString());
        genericTemplateModelData.setObjectTitle(notification.getParentTitle());

        AcmAbstractDao<AcmObject> dao = getDataService().getDaoByObjectType(notification.getParentType());
        AcmObject acmObject = dao.find(notification.getParentId());

        if(acmObject instanceof AcmAssignedObject)
        {
            AcmAssignedObject acmAssignedObject = (AcmAssignedObject)acmObject;
            AcmParticipant assignee = acmAssignedObject.getParticipants().stream().filter(acmParticipant -> acmParticipant.getParticipantType()
                    .equals("assignee")).findFirst().orElse(null);

            String baseUserId = "";
            if(Objects.nonNull(assignee))
            {
                AcmUser user = getUserDao().findByUserId(assignee.getParticipantLdapId());
                String directoryName = user.getUserDirectoryName();

                if(!directoryName.isEmpty())
                {
                    AcmLdapSyncConfig acmLdapSyncConfig = getContextHolder().getBeanByNameIncludingChildContexts(directoryName.concat("_sync"), AcmLdapSyncConfig.class);
                    String userPrefix = acmLdapSyncConfig.getUserPrefix();
                    if (StringUtils.isNotBlank(userPrefix))
                    {
                        log.debug(String.format("User Prefix [%s]", userPrefix));
                        log.debug(String.format("Full User id: [%s]", baseUserId));
                        baseUserId = assignee.getParticipantLdapId().replace(userPrefix.concat("."), "");
                        log.debug(String.format("User Id without prefix: [%s]", baseUserId));
                    }
                    else
                    {
                        baseUserId = assignee.getParticipantLdapId();
                    }

                }
                else
                {
                    baseUserId = assignee.getParticipantLdapId();
                }
            }

            genericTemplateModelData.setOtherObjectValue(baseUserId);
        }

        return genericTemplateModelData;
    }

    @Override
    public Class<GenericTemplateModel> getType()
    {
        return GenericTemplateModel.class;
    }

    public AcmDataService getDataService()
    {
        return dataService;
    }

    public void setDataService(AcmDataService dataService)
    {
        this.dataService = dataService;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public SpringContextHolder getContextHolder()
    {
        return contextHolder;
    }

    public void setContextHolder(SpringContextHolder contextHolder)
    {
        this.contextHolder = contextHolder;
    }
}

