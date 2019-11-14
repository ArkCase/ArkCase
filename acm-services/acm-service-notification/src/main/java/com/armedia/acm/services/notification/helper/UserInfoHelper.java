package com.armedia.acm.services.notification.helper;

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

import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.spring.SpringContextHolder;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserInfoHelper
{
    private Logger log = LogManager.getLogger(getClass().getName());

    private UserDao userDao;
    private SpringContextHolder contextHolder;
    private AcmGroupDao acmGroupDao;

    public String getUserEmail(String userId)
    {
        AcmUser user = getUserDao().findByUserId(userId);
        return user.getMail();
    }

    public String removeUserPrefix(String userId)
    {
        AcmUser user = getUserDao().findByUserId(userId);
        String directoryName = user.getUserDirectoryName();

        String baseUserId = user.getUserId();

        if (StringUtils.isNotBlank(directoryName))
        {
            try
            {
                AcmLdapSyncConfig acmLdapSyncConfig = getContextHolder().getBeanByNameIncludingChildContexts(directoryName.concat("_sync"),
                        AcmLdapSyncConfig.class);
                String userPrefix = acmLdapSyncConfig.getUserPrefix();
                if (StringUtils.isNotBlank(userPrefix))
                {
                    log.debug(String.format("User Prefix [%s]", userPrefix));
                    log.debug(String.format("Full User id: [%s]", baseUserId));
                    baseUserId = user.getUserId().replace(userPrefix, "");
                    log.debug(String.format("User Id without prefix: [%s]", baseUserId));
                }
            }
            catch (Exception e)
            {
                log.debug("Error processing user prefix", e);
            }
        }

        return baseUserId;
    }

    public String removeGroupPrefix(String groupId)
    {
        AcmGroup acmGroup = getAcmGroupDao().findByName(groupId);
        String directoryName = acmGroup.getDirectoryName();

        String baseGroupId = acmGroup.getName();

        if (StringUtils.isNotBlank(directoryName))
        {
            try
            {
                AcmLdapSyncConfig acmLdapSyncConfig = getContextHolder().getBeanByNameIncludingChildContexts(directoryName.concat("_sync"),
                        AcmLdapSyncConfig.class);
                String groupPrefix = acmLdapSyncConfig.getGroupPrefix();
                if (StringUtils.isNotBlank(groupPrefix))
                {
                    log.debug(String.format("Group Prefix [%s]", groupPrefix));
                    log.debug(String.format("Full Group Name [%s]", baseGroupId));
                    baseGroupId = groupId.replace(groupPrefix, "");
                    log.debug(String.format("Group Name without prefix: [%s]", baseGroupId));
                }
            }
            catch (Exception e)
            {
                log.debug("Error processing group prefix", e);
            }
        }

        return baseGroupId;
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

    public AcmGroupDao getAcmGroupDao()
    {
        return acmGroupDao;
    }

    public void setAcmGroupDao(AcmGroupDao acmGroupDao)
    {
        this.acmGroupDao = acmGroupDao;
    }
}
