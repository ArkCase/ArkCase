package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmRole;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserRole;
import com.armedia.acm.services.users.model.group.AcmGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class LdapDatabaseSyncService
{
    private final static Logger log = LoggerFactory.getLogger(LdapDatabaseSyncService.class);
    private UserDao userDao;
    private AcmGroupDao groupDao;

    public List<AcmUser> saveUsers(AcmUsersSyncResult acmUsersSyncResult)
    {
        List<AcmUser> savedUsers = new ArrayList<>();
        log.info("Saving new users [{}]", acmUsersSyncResult.getNewUsers().size());
        acmUsersSyncResult.getNewUsers().forEach(acmUser -> {
            log.info("Saving AcmUser [{}]", acmUser.getUserId());
            userDao.save(acmUser);
            savedUsers.add(acmUser);
        });

        log.info("Updating existing users [{}]", acmUsersSyncResult.getChangedUsers().size());
        acmUsersSyncResult.getChangedUsers().forEach(acmUser -> {
            log.info("Updating AcmUser [{}]", acmUser.getUserId());
            userDao.save(acmUser);
            savedUsers.add(acmUser);
        });

        userDao.getEm().flush();
        return savedUsers;
    }

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

    public void saveAcmRoles(List<String> applicationRoles, String roleType)
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

    public void saveAcmUserRoles(List<AcmUserRole> acmUserRoles)
    {
        log.info("Saving AcmUserRoles [{}]", acmUserRoles.size());
        acmUserRoles.forEach(userRole -> {
            log.info("Saving AcmUserRole [{}] for user [{}]", userRole.getRoleName(), userRole.getUserId());
            getUserDao().saveAcmUserRole(userRole);
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
