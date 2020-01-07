package com.armedia.acm.services.users.service;

/*-
 * #%L
 * ACM Service: Users
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

import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmRole;
import com.armedia.acm.services.users.model.AcmRoleToGroupMapping;
import com.armedia.acm.services.users.model.AcmRoleType;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AcmUserRoleService
{
    private UserDao userDao;
    private AcmRoleToGroupMapping roleToGroupConfig;
    private Logger log = LogManager.getLogger(getClass());

    public Set<String> getUserRoles(String userId)
    {
        Map<String, List<String>> groupToRoleMap = roleToGroupConfig.getGroupToRolesMap();

        AcmUser user = userDao.findByUserId(userId);
        Set<AcmGroup> userGroups = user.getGroups();

        Set<String> userGroupNames = Stream
                .concat(userGroups.stream().map(AcmGroup::getName).map(String::toUpperCase),
                        userGroups.stream().flatMap(AcmGroup::getAscendantsStream).map(String::toUpperCase))
                .collect(Collectors.toSet());

        return userGroupNames.stream()
                .filter(groupToRoleMap::containsKey)
                .flatMap(g -> groupToRoleMap.get(g).stream())
                .collect(Collectors.toSet());
    }

    @Transactional
    public void saveRolesPerAddedGroups(Set<AcmGroup> groups)
    {
        // added groups are also valid AcmRoles
        groups.forEach(group -> {
            AcmRole role = new AcmRole();
            role.setRoleType(AcmRoleType.valueOf(group.getType().name()));
            role.setRoleName(group.getName());
            userDao.saveAcmRole(role);
        });

        userDao.getEntityManager().flush();
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public void setRoleToGroupConfig(AcmRoleToGroupMapping roleToGroupConfig)
    {
        this.roleToGroupConfig = roleToGroupConfig;
    }
}
