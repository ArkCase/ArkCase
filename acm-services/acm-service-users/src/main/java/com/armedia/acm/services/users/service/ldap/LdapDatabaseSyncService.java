package com.armedia.acm.services.users.service.ldap;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.model.AcmRole;
import com.armedia.acm.services.users.model.AcmRoleType;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;

public class LdapDatabaseSyncService
{
    private final static Logger log = LoggerFactory.getLogger(LdapDatabaseSyncService.class);
    private UserDao userDao;
    private AcmGroupDao groupDao;

    @Transactional
    public List<AcmUser> saveUsers(AcmUsersSyncResult acmUsersSyncResult)
    {
        List<AcmUser> savedUsers = new ArrayList<>();

        // filter out users that are not members to any AcmGroup
        Set<AcmUser> newUsers = acmUsersSyncResult.getNewUsers().stream().filter(user -> !user.getGroups().isEmpty())
                .collect(Collectors.toSet());
        log.info("Saving new users [{}]", newUsers.size());
        newUsers.forEach(acmUser -> {
            log.info("Saving AcmUser [{}]", acmUser.getUserId());
            userDao.persistUser(acmUser);
            savedUsers.add(acmUser);
        });

        log.info("Updating existing users [{}]", acmUsersSyncResult.getChangedUsers().size());
        acmUsersSyncResult.getChangedUsers().forEach(acmUser -> {
            log.info("Updating AcmUser [{}]", acmUser.getUserId());
            acmUser = userDao.save(acmUser);
            savedUsers.add(acmUser);
        });
        return savedUsers;
    }

    @Transactional
    public List<AcmGroup> saveGroups(AcmGroupsSyncResult acmGroupsSyncResult)
    {
        List<AcmGroup> savedGroups = new ArrayList<>();
        log.info("Saving new groups [{}]", acmGroupsSyncResult.getNewGroups().size());
        acmGroupsSyncResult.getNewGroups().forEach(acmGroup -> {
            log.info("Saving AcmGroup [{}]", acmGroup.getName());
            groupDao.save(acmGroup);
            savedGroups.add(acmGroup);
        });

        log.info("Updating existing groups [{}]", acmGroupsSyncResult.getChangedGroups().size());
        acmGroupsSyncResult.getChangedGroups().forEach(acmGroup -> {
            log.info("Updating AcmGroup [{}]", acmGroup.getName());
            groupDao.save(acmGroup);
            savedGroups.add(acmGroup);
        });
        return savedGroups;
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
}
