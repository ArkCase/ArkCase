package com.armedia.acm.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;

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


public class AcmGrantedAuthoritiesMapper
{
    
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    /** These properties are loaded by Spring from a properties file in the user's
     * home folder.  Default mappings are used if
     * the properties file does not exist.
     */
    private Properties applicationRoleToUserGroupProperties;
  
    /** This mapping is the effective mapping used when a user logs in to see 
     * which roles they will have.  It is populated during "initBean()" method 
     * execution.
     */
    private Map<String, List<String>> activeMapping;
    
    /**
     * Read the role mapping file and set the group mapping properties.  The 
     * role mapping file must be a properties file with one key for each application role.
     * The value must be the group name whose members will have that role.
     */
    public void initBean()
    {
        if ( getApplicationRoleToUserGroupProperties() != null && ! getApplicationRoleToUserGroupProperties().isEmpty() )
        {
            Map<String, List<String>> groupsToRoles = mapGroupsToRoles();
            setActiveMapping(groupsToRoles);
            
            logProperties(getApplicationRoleToUserGroupProperties());
        }
        else
        {
            log.error("role to group mapping is not configured - no one "
                    + "will be able to log in!");
            setActiveMapping(new HashMap<String, List<String>>());
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

        for (GrantedAuthority authority : grantedAuthorities) {
            if ( authority == null || authority.getAuthority() == null || 
                    authority.getAuthority().trim().isEmpty() ) 
            {
                continue;
            }
            
            String groupName = authority.getAuthority().trim().toUpperCase();
            
            log.debug(String.format("Incoming group: [%s]", groupName));
            
            List<String> groupRoles = getActiveMapping().get(groupName);
            
            if ( groupRoles != null ) 
            {
                for ( String groupRole : groupRoles )
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
        for ( String ldapGroup : ldapGroups )
        {
            if ( ldapGroup == null || ldapGroup.trim().isEmpty() )
            {
                continue;
            }
            ldapGroup = ldapGroup.trim().toUpperCase();
            List<String> rolesForGroup = getActiveMapping().get(ldapGroup);
            if ( rolesForGroup != null && !rolesForGroup.isEmpty() )
            {
                for ( String role : rolesForGroup )
                {
                    String appRole = role.replaceFirst("ROLE_", "").toLowerCase();
                    if ( !retval.contains(appRole) )
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
        for ( Entry<Object, Object> entry : getApplicationRoleToUserGroupProperties().entrySet() )
        {
            String role = (String) entry.getKey();
            String group = (String) entry.getValue();
            
            if ( role == null || role.trim().isEmpty() || 
                    group == null || group.trim().isEmpty() ) 
            {
                continue;
            }
            
            role = role.trim().toUpperCase();
            if ( ! role.startsWith("ROLE_") ) 
            {
                role = "ROLE_" + role;
            }
            group = group.trim().toUpperCase();
            
            if ( groupsToRoles.containsKey(group) )
            {
                List<String> roles = groupsToRoles.get(group);
                roles.add(role);
            }
            else
            {
                List<String> roles = new ArrayList<>();
                roles.add(role);
                groupsToRoles.put(group, roles);
            }
        }
        return groupsToRoles;
    }
    
    
}
