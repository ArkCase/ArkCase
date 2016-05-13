package com.armedia.acm.auth;

import com.armedia.acm.files.ConfigurationFileChangedEvent;
import com.armedia.acm.files.propertymanager.PropertyFileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.GrantedAuthority;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;


public class AcmGrantedAuthoritiesMapper implements ApplicationListener<ConfigurationFileChangedEvent>
{

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * These properties are loaded by Spring from a properties file in the user's
     * home folder.  Default mappings are used if
     * the properties file does not exist.
     */
    private Properties applicationRoleToUserGroupProperties;

    /**
     * This mapping is the effective mapping used when a user logs in to see
     * which roles they will have.  It is populated during "initBean()" method
     * execution.
     */
    private Map<String, List<String>> activeMapping;
    private PropertyFileManager propertyFileManager;

    /**
     * Read the role mapping file and set the group mapping properties.  The
     * role mapping file must be a properties file with one key for each application role.
     * The value must be the group name whose members will have that role.
     */
    public void initBean()
    {
        if (getApplicationRoleToUserGroupProperties() != null && !getApplicationRoleToUserGroupProperties().isEmpty())
        {
            Map<String, List<String>> groupsToRoles = mapGroupsToRoles();
            setActiveMapping(groupsToRoles);

            logProperties(getApplicationRoleToUserGroupProperties());
        } else
        {
            log.error("role to group mapping is not configured - no one "
                    + "will be able to log in!");
            setActiveMapping(new HashMap<>());
        }
    }

    @Override
    public void onApplicationEvent(ConfigurationFileChangedEvent configurationFileChangedEvent)
    {
        File eventFile = configurationFileChangedEvent.getConfigFile();
        if ("applicationRoleToUserGroup.properties".equals(eventFile.getName()))
        {
            String filename = eventFile.getName();
            log.info("[{}] has changed!", filename);

            try
            {
                Properties reloaded = getPropertyFileManager().readFromFile(eventFile);
                applicationRoleToUserGroupProperties = reloaded;
                initBean();
            } catch (IOException e)
            {
                log.info("Could not read new properties; keeping the old properties.");
            }

        }


    }

    private void logProperties(Properties p)
    {
        StringWriter out = new StringWriter();
        PrintWriter pw = new PrintWriter(out);
        p.list(pw);
        String propString = out.toString();
        String message = "Role to Group Mapping Properties: \n" + propString;
        log.info(message);
    }

    public Collection<AcmGrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> grantedAuthorities)
    {
        Set<AcmGrantedAuthority> roles = new HashSet<>();

        for (GrantedAuthority authority : grantedAuthorities)
        {
            if (authority == null || authority.getAuthority() == null ||
                    authority.getAuthority().trim().isEmpty())
            {
                continue;
            }

            String groupName = authority.getAuthority().trim().toUpperCase();

            log.debug(String.format("Incoming group: [%s]", groupName));

            List<String> groupRoles = getActiveMapping().get(groupName);

            if (groupRoles != null)
            {
                for (String groupRole : groupRoles)
                {
                    log.debug(String.format("role for group [%s] = [%s]",
                            groupName, groupRole));

                    AcmGrantedAuthority auth = new AcmGrantedAuthority(groupRole);
                    roles.add(auth);
                }
            }

        }

        return roles;
    }

    public List<String> applicationGroupsFromLdapGroups(List<String> ldapGroups)
    {

        List<String> retval = new ArrayList<>();
        for (String ldapGroup : ldapGroups)
        {
            if (ldapGroup == null || ldapGroup.trim().isEmpty())
            {
                continue;
            }
            ldapGroup = ldapGroup.trim().toUpperCase();
            List<String> rolesForGroup = getActiveMapping().get(ldapGroup);
            if (rolesForGroup != null && !rolesForGroup.isEmpty())
            {
                for (String role : rolesForGroup)
                {
                    String appRole = role.replaceFirst("ROLE_", "").toLowerCase();
                    if (!retval.contains(appRole))
                    {
                        retval.add(appRole);
                    }
                }
            }
        }

        return retval;
    }

    public Properties getApplicationRoleToUserGroupProperties()
    {
        return applicationRoleToUserGroupProperties;
    }

    public void setApplicationRoleToUserGroupProperties(Properties applicationRoleToUserGroupProperties)
    {
        this.applicationRoleToUserGroupProperties = applicationRoleToUserGroupProperties;
    }

    public Map<String, List<String>> getActiveMapping()
    {
        return activeMapping;
    }

    public void setActiveMapping(Map<String, List<String>> activeMapping)
    {
        this.activeMapping = activeMapping;
    }

    private Map<String, List<String>> mapGroupsToRoles()
    {
        Map<String, List<String>> groupsToRoles = new HashMap<>();
        for (Entry<Object, Object> entry : getApplicationRoleToUserGroupProperties().entrySet())
        {
            String role = (String) entry.getKey();
            String groups = (String) entry.getValue();

            if (role == null || role.trim().isEmpty() ||
                    groups == null || groups.trim().isEmpty())
            {
                continue;
            }

            role = role.trim().toUpperCase();
            if (!role.startsWith("ROLE_"))
            {
                role = "ROLE_" + role;
            }

            // Groups for each role are comma separated in the property file. Go through all groups for each role
            String[] groupsArray = groups.split(",");

            if (groupsArray != null && groupsArray.length > 0)
            {
                for (String group : groupsArray)
                {
                    group = group.trim().toUpperCase();

                    if (groupsToRoles.containsKey(group))
                    {
                        List<String> roles = groupsToRoles.get(group);
                        roles.add(role);
                    } else
                    {
                        List<String> roles = new ArrayList<>();
                        roles.add(role);
                        groupsToRoles.put(group, roles);
                    }
                }
            }
        }
        return groupsToRoles;
    }


    public void setPropertyFileManager(PropertyFileManager propertyFileManager)
    {
        this.propertyFileManager = propertyFileManager;
    }

    public PropertyFileManager getPropertyFileManager()
    {
        return propertyFileManager;
    }
}
