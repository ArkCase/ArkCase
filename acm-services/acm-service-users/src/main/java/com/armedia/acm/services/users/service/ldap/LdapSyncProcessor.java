package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmRoleType;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.LdapGroup;
import com.armedia.acm.services.users.model.LdapUser;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
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
    private LdapDatabaseSyncService ldapDatabaseSyncService;

    @Transactional
    public void sync(List<LdapUser> ldapUsers, List<LdapGroup> ldapGroups, AcmLdapSyncConfig ldapSyncConfig)
    {
        List<AcmUser> currentUsers = userDao.findByDirectory(ldapSyncConfig.getDirectoryName());
        AcmUsersSyncResult acmUsersSyncResult = new AcmUsersSyncResult();
        Map<String, AcmUser> allUsersByIdMap = acmUsersSyncResult.sync(ldapUsers, currentUsers);

        List<AcmGroup> acmGroups = groupDao.findLdapGroupsByDirectory(ldapSyncConfig.getDirectoryName());
        AcmGroupsSyncResult acmGroupsSyncResult = new AcmGroupsSyncResult();
        Map<String, AcmGroup> allGroupsByIdMap = acmGroupsSyncResult.sync(ldapGroups, acmGroups, allUsersByIdMap);

        Map<String, Set<String>> userGroupsMap = acmGroupsSyncResult.getGroupsByUserIdMap(allGroupsByIdMap);

        Map<String, String> roleToGroupsString = ldapSyncConfig.getRoleToGroupMap();
        Map<String, Set<String>> roleToGroup = LdapSyncService.roleToGroups(roleToGroupsString);
        Map<String, List<String>> groupToRoleMap = LdapSyncService.reverseRoleToGroupMap(roleToGroupsString);

        AcmUserRolesSyncResult acmUserRolesSyncResult = new AcmUserRolesSyncResult(acmGroupsSyncResult.getUserNewGroups(),
                acmGroupsSyncResult.getUserRemovedGroups(), groupToRoleMap, userGroupsMap);

        ldapDatabaseSyncService.saveUsers(acmUsersSyncResult);

        ldapDatabaseSyncService.saveGroups(acmGroupsSyncResult);

        List<String> applicationRoles = new ArrayList<>(roleToGroup.keySet());
        ldapDatabaseSyncService.saveAcmRoles(applicationRoles, AcmRoleType.APPLICATION_ROLE.getRoleName());

        List<String> newAcmGroups = acmGroupsSyncResult.getNewGroups().stream()
                .map(AcmGroup::getName)
                .collect(Collectors.toList());
        ldapDatabaseSyncService.saveAcmRoles(newAcmGroups, AcmRoleType.LDAP_GROUP.getRoleName());

        ldapDatabaseSyncService.saveAcmUserRoles(acmUserRolesSyncResult.getAcmUserRoles());
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
}
