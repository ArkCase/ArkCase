package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.model.AcmRole;
import com.armedia.acm.services.users.model.AcmRoleType;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserState;

import com.armedia.acm.services.users.service.AcmGroupEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LdapDatabaseSyncService
{
    private final static Logger log = LoggerFactory.getLogger(LdapDatabaseSyncService.class);
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
