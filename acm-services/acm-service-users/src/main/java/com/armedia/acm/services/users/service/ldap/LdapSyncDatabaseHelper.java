package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmRole;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by armdev on 5/29/14.
 */
public class LdapSyncDatabaseHelper
{
    private UserDao userDao;
    private Logger log = LoggerFactory.getLogger(getClass());

    @Transactional
    public void updateDatabase(Set<String> allRoles, Map<String, List<AcmUser>> usersByDirectoryName, Map<String, List<AcmUser>> usersByRole)
    {
        // Mark all users invalid... users still in LDAP will change to valid during the sync
        getUserDao().markAllUsersInvalid();
        getUserDao().markAllRolesInvalid();

        persistApplicationRoles(allRoles);

        for ( Map.Entry<String, List<AcmUser>> usersByDirectoryNameEntry : usersByDirectoryName.entrySet() )
        {
            persistUsers(usersByDirectoryNameEntry.getValue(), usersByDirectoryNameEntry.getKey());
        }

        for ( Map.Entry<String, List<AcmUser>> usersByRoleEntry : usersByRole.entrySet() )
        {
            persistUserRoles(usersByRoleEntry.getValue(), usersByRoleEntry.getKey());
        }
    }

    private List<AcmUserRole> persistUserRoles(List<AcmUser> savedUsers, String roleName)
    {
        List<AcmUserRole> retval = new ArrayList<>(savedUsers.size());

        boolean debug = log.isDebugEnabled();

        for ( AcmUser user : savedUsers )
        {
            if ( debug )
            {
                log.debug("persisting user role '" + user.getUserId() + ", " + roleName + "'");
            }

            AcmUserRole role = new AcmUserRole();
            role.setUserId(user.getUserId());
            role.setRoleName(roleName);

            role = getUserDao().saveAcmUserRole(role);
            retval.add(role);

        }

        return retval;
    }

    protected List<AcmUser> persistUsers(List<AcmUser> users, String configBeanName)
    {
        List<AcmUser> retval = new ArrayList<>(users.size());

        boolean debug = log.isDebugEnabled();

        for ( AcmUser user : users )
        {
            if ( debug )
            {
                log.debug("persisting user '" + user.getUserId() + "'");
            }

            user.setUserDirectoryName(configBeanName);
            AcmUser saved = getUserDao().saveAcmUser(user);
            retval.add(saved);
        }

        return retval;
    }

    protected void persistApplicationRoles(Set<String> applicationRoles)
    {
        boolean debug = log.isDebugEnabled();
        for ( String role : applicationRoles )
        {
            if ( debug )
            {
                log.debug("persisting role '" + role + "'");
            }
            AcmRole jpaRole = new AcmRole();
            jpaRole.setRoleName(role);
            getUserDao().saveAcmRole(jpaRole);
        }
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }
}
