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
import com.armedia.acm.services.users.model.AcmRole;
import com.armedia.acm.services.users.model.AcmRoleType;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserState;
import com.armedia.acm.services.users.service.AcmGroupEventPublisher;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LdapDatabaseSyncService
{
    private final static Logger log = LogManager.getLogger(LdapDatabaseSyncService.class);
    private UserDao userDao;
    private AcmGroupDao groupDao;
    private AcmGroupEventPublisher acmGroupEventPublisher;

    @Transactional
    public void saveUsers(AcmUsersSyncResult acmUsersSyncResult)
    {
        // filter out users that are not members to any AcmGroup
        Set<AcmUser> newUsers = acmUsersSyncResult.getNewUsers().stream()
                .filter(user -> !user.getGroups().isEmpty())
                .collect(Collectors.toSet());
        log.info("Saving new users [{}]", newUsers.size());
        newUsers.forEach(acmUser -> {
            log.info("Saving AcmUser [{}]", acmUser.getUserId());
            acmUser.getGroups().clear();
            userDao.persistUser(acmUser);
        });

        log.info("Updating existing users [{}]", acmUsersSyncResult.getModifiedUsers().size());
        acmUsersSyncResult.getModifiedUsers().forEach(acmUser -> {
            log.info("Updating AcmUser [{}]", acmUser.getUserId());
            userDao.save(acmUser);
        });

        acmUsersSyncResult.getDeletedUsers().forEach(acmUser -> {
            log.info("Set AcmUser [{}] as [{}]", acmUser.getUserId(), AcmUserState.INVALID);
            userDao.save(acmUser);
        });
    }

    @Transactional
    public void saveGroups(AcmGroupsSyncResult acmGroupsSyncResult)
    {
        log.info("Saving new groups [{}]", acmGroupsSyncResult.getNewGroups().size());
        acmGroupsSyncResult.getNewGroups().forEach(acmGroup -> {
            log.info("Saving AcmGroup [{}]", acmGroup.getName());
            groupDao.save(acmGroup);
            acmGroupEventPublisher.publishLdapGroupCreatedEvent(acmGroup);
        });

        log.info("Updating existing groups [{}]", acmGroupsSyncResult.getModifiedGroups().size());
        acmGroupsSyncResult.getModifiedGroups().forEach(acmGroup -> {
            log.info("Updating AcmGroup [{}]", acmGroup.getName());
            groupDao.save(acmGroup);
        });

        log.info("Updating deleted groups [{}]", acmGroupsSyncResult.getDeletedGroups().size());
        acmGroupsSyncResult.getDeletedGroups().forEach(acmGroup -> {
            log.info("Updating AcmGroup [{}]", acmGroup.getName());
            groupDao.save(acmGroup);
            acmGroupEventPublisher.publishLdapGroupDeletedEvent(acmGroup);
        });
    }

    @Transactional
    public void saveAcmRoles(List<String> applicationRoles, AcmRoleType roleType)
    {
        log.info("Saving AcmRoles [{}]", applicationRoles.size());
        applicationRoles.forEach(role -> {
            AcmRole acmRole = new AcmRole();
            acmRole.setRoleName(role);
            acmRole.setRoleType(roleType);
            log.info("Saving AcmRole [{}]", role);
            getUserDao().saveAcmRole(acmRole);
        });
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

    public void setAcmGroupEventPublisher(AcmGroupEventPublisher acmGroupEventPublisher)
    {
        this.acmGroupEventPublisher = acmGroupEventPublisher;
    }
}
