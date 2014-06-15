package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.files.ConfigFileWatcher;
import com.armedia.acm.services.users.dao.ldap.SpringLdapDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.spring.SpringContextHolder;
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

    private SpringContextHolder springContextHolder;
    private ConfigFileWatcher configFileWatcher;
    private SpringLdapDao ldapDao;
    private LdapSyncDatabaseHelper ldapSyncDatabaseHelper;

    private Logger log = LoggerFactory.getLogger(getClass());

    public void ldapSync()
    {
        Map<String, AcmLdapSyncConfig> configMap = getSpringContextHolder().getAllBeansOfType(AcmLdapSyncConfig.class);


        if ( log.isInfoEnabled() )
        {
            log.info("# of LDAP configs to sync: " + configMap.size());
        }

        // all the ldap work first, then all the database work; because the ldap queries could be very timeconsuming.
        // If we opened up a database transaction, then spend a minute or so querying LDAP, the database transaction
        // could time out.  So we query every LDAP sync target first, then do all the database operations all at once.
        Set<String> allRoles = new HashSet<>();
        Map<String, List<AcmUser>> usersByDirectoryName = new HashMap<>();
        Map<String, List<AcmUser>> usersByRole = new HashMap<>();

        queryLdapUsers(configMap, allRoles, usersByDirectoryName, usersByRole);

        // ldap work is done.  now for the database work.
        getLdapSyncDatabaseHelper().updateDatabase(allRoles, usersByDirectoryName, usersByRole);
    }



    protected void queryLdapUsers(
            Map<String, AcmLdapSyncConfig> configMap,
            Set<String> allRoles,
            Map<String, List<AcmUser>> usersByDirectoryName,
            Map<String, List<AcmUser>> usersByRole)
    {
        for ( Map.Entry<String, AcmLdapSyncConfig> config : configMap.entrySet() )
        {

            AcmLdapSyncConfig syncConfig = config.getValue();
            String directoryName = config.getKey();

            if ( log.isDebugEnabled() )
            {
                log.debug("querying users from directory '" + directoryName + "'");
            }

            LdapTemplate template = getLdapDao().buildLdapTemplate(syncConfig);

            Map<String, String> roleToGroup = syncConfig.getRoleToGroupMap();
            allRoles.addAll(roleToGroup.keySet());

            for ( Map.Entry<String, String> roleToGroupEntry : roleToGroup.entrySet() )
            {
                String role = roleToGroupEntry.getKey();
                String ldapGroup = roleToGroupEntry.getValue();
                List<AcmUser> users = getLdapDao().findGroupMembers(template, syncConfig, ldapGroup);
                addUsersToMap(usersByDirectoryName, directoryName, users);
                addUsersToMap(usersByRole, role, users);
            }
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

    public SpringContextHolder getSpringContextHolder()
    {
        return springContextHolder;
    }

    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
    }

    public ConfigFileWatcher getConfigFileWatcher()
    {
        return configFileWatcher;
    }

    public void setConfigFileWatcher(ConfigFileWatcher configFileWatcher)
    {
        this.configFileWatcher = configFileWatcher;
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
}
