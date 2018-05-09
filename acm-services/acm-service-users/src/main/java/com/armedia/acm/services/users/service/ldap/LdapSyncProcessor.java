package com.armedia.acm.services.users.service.ldap;

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
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.model.AcmRoleToGroupMapping;
import com.armedia.acm.services.users.model.AcmRoleType;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.model.ldap.LdapGroup;
import com.armedia.acm.services.users.model.ldap.LdapUser;

import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class LdapSyncProcessor
{
    private UserDao userDao;
    private AcmGroupDao groupDao;
    private AcmRoleToGroupMapping roleToGroupConfig;
    private LdapDatabaseSyncService ldapDatabaseSyncService;

    @Transactional
    public void sync(List<LdapUser> ldapUsers, List<LdapGroup> ldapGroups, AcmLdapSyncConfig ldapSyncConfig, boolean fullSync)
    {
        List<AcmUser> acmUsers = userDao.findByDirectory(ldapSyncConfig.getDirectoryName());
        AcmUsersSyncResult acmUsersSyncResult = new AcmUsersSyncResult(fullSync);
        Map<String, AcmUser> acmSyncedUsers = acmUsersSyncResult.sync(ldapUsers, acmUsers, userDao.getDefaultUserLang());

        List<AcmGroup> acmGroups = groupDao.findLdapGroupsByDirectory(ldapSyncConfig.getDirectoryName());
        AcmGroupsSyncResult acmGroupsSyncResult = new AcmGroupsSyncResult();
        acmGroupsSyncResult.sync(ldapGroups, acmGroups, acmSyncedUsers);

        ldapDatabaseSyncService.saveUsers(acmUsersSyncResult);

        ldapDatabaseSyncService.saveGroups(acmGroupsSyncResult);

        Map<String, Set<String>> roleToGroup = roleToGroupConfig.getRoleToGroupsMap();
        List<String> applicationRoles = new ArrayList<>(roleToGroup.keySet());
        ldapDatabaseSyncService.saveAcmRoles(applicationRoles, AcmRoleType.APPLICATION_ROLE);

        List<String> newAcmGroups = acmGroupsSyncResult.getNewGroups().stream().map(AcmGroup::getName).collect(Collectors.toList());
        ldapDatabaseSyncService.saveAcmRoles(newAcmGroups, AcmRoleType.LDAP_GROUP);

    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public void setGroupDao(AcmGroupDao groupDao)
    {
        this.groupDao = groupDao;
    }

    public void setLdapDatabaseSyncService(LdapDatabaseSyncService ldapDatabaseSyncService)
    {
        this.ldapDatabaseSyncService = ldapDatabaseSyncService;
    }

    public void setRoleToGroupConfig(AcmRoleToGroupMapping roleToGroupConfig)
    {
        this.roleToGroupConfig = roleToGroupConfig;
    }
}
