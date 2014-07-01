package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.services.users.dao.ldap.SpringLdapDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.LdapTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by armdev on 5/28/14.
 */
public class LdapSyncService
{
    private SpringLdapDao ldapDao;
    private LdapSyncDatabaseHelper ldapSyncDatabaseHelper;
    private AcmLdapSyncConfig ldapSyncConfig;
    private String directoryName;


    private Logger log = LoggerFactory.getLogger(getClass());

    // this method is used by scheduled jobs in Spring beans loaded dynamically from the ACM configuration
    // folder ($HOME/.acm).
    public void ldapSync()
    {
        if ( log.isInfoEnabled() )
        {
            log.info("Starting sync of directory: " + getDirectoryName() + "; ldap URL: " +
                    getLdapSyncConfig().getLdapUrl());
        }

        // all the ldap work first, then all the database work; because the ldap queries could be very timeconsuming.
        // If we opened up a database transaction, then spend a minute or so querying LDAP, the database transaction
        // could time out.  So we query every LDAP sync target first, then do all the database operations all at once.
        Set<String> allRoles = new HashSet<>();
        List<AcmUser> users = new ArrayList<>();
        Map<String, List<AcmUser>> usersByRole = new HashMap<>();

        queryLdapUsers(getLdapSyncConfig(), getDirectoryName(), allRoles, users, usersByRole);

        // ldap work is done.  now for the database work.
        getLdapSyncDatabaseHelper().updateDatabase(getDirectoryName(), allRoles, users, usersByRole);
    }



    protected void queryLdapUsers(
            AcmLdapSyncConfig config,
            String directoryName,
            Set<String> allRoles,
            List<AcmUser> users,
            Map<String, List<AcmUser>> usersByRole)
    {
        if ( log.isDebugEnabled() )
        {
            log.debug("querying users from directory '" + directoryName + "'");
        }

        LdapTemplate template = getLdapDao().buildLdapTemplate(config);

        Map<String, String> roleToGroup = config.getRoleToGroupMap();
        allRoles.addAll(roleToGroup.keySet());

        for ( Map.Entry<String, String> roleToGroupEntry : roleToGroup.entrySet() )
        {
            String role = roleToGroupEntry.getKey();
            String ldapGroup = roleToGroupEntry.getValue();
            List<AcmUser> foundUsers = getLdapDao().findGroupMembers(template, config, ldapGroup);
            users.addAll(foundUsers);
            addUsersToMap(usersByRole, role, users);
        }
    }

    private void addUsersToMap(Map<String, List<AcmUser>> userMap, String mapKey, List<AcmUser> users)
    {
        // ensure to initialize the map entry with an empty list, then copy from the incoming list into the new list.
        if ( ! userMap.containsKey(mapKey) )
        {
            userMap.put(mapKey, new ArrayList<AcmUser>());
        }
        userMap.get(mapKey).addAll(users);
    }

    public SpringLdapDao getLdapDao()
    {
        return ldapDao;
    }

    public void setLdapDao(SpringLdapDao ldapDao)
    {
        this.ldapDao = ldapDao;
    }

    public LdapSyncDatabaseHelper getLdapSyncDatabaseHelper()
    {
        return ldapSyncDatabaseHelper;
    }

    public void setLdapSyncDatabaseHelper(LdapSyncDatabaseHelper ldapSyncDatabaseHelper)
    {
        this.ldapSyncDatabaseHelper = ldapSyncDatabaseHelper;
    }

    public AcmLdapSyncConfig getLdapSyncConfig()
    {
        return ldapSyncConfig;
    }

    public void setLdapSyncConfig(AcmLdapSyncConfig ldapSyncConfig)
    {
        this.ldapSyncConfig = ldapSyncConfig;
    }

    public String getDirectoryName()
    {
        return directoryName;
    }

    public void setDirectoryName(String directoryName)
    {
        this.directoryName = directoryName;
    }
}
