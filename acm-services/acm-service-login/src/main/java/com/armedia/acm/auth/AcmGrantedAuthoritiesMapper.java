package com.armedia.acm.auth;

import com.armedia.acm.files.ConfigurationFileChangedEvent;
import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.services.users.model.AcmRoleToGroupMapping;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.GrantedAuthority;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AcmGrantedAuthoritiesMapper implements ApplicationListener<ConfigurationFileChangedEvent>
{

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * These properties are loaded by Spring from a properties file in the user's
     * home folder. Default mappings are used if
     * the properties file does not exist.
     */
    private Properties applicationRoleToUserGroupProperties;

    /**
     * This mapping is the effective mapping used when a user logs in to see
     * which roles they will have. It is populated during "initBean()" method
     * execution.
     */
    private Map<String, List<String>> activeMapping;

    private PropertyFileManager propertyFileManager;

    private AcmRoleToGroupMapping roleToGroupMapping;

    /**
     * Read the role mapping file and set the group mapping properties. The
     * role mapping file must be a properties file with one key for each application role.
     * The value must be the group name whose members will have that role.
     */
    public void initBean()
    {
        if (applicationRoleToUserGroupProperties != null && !applicationRoleToUserGroupProperties.isEmpty())
        {
            roleToGroupMapping.reloadRoleToGroupMap(applicationRoleToUserGroupProperties);
            Map<String, List<String>> groupsToRoles = roleToGroupMapping.getGroupToRolesMap();
            setActiveMapping(groupsToRoles);
            logProperties(applicationRoleToUserGroupProperties);
        }
        else
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
                applicationRoleToUserGroupProperties = propertyFileManager.readFromFile(eventFile);
                initBean();
            }
            catch (IOException e)
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
        Predicate<GrantedAuthority> authorityNotBlank = authority -> authority != null &&
                StringUtils.isNotBlank(authority.getAuthority());

        return grantedAuthorities.stream()
                .filter(authorityNotBlank)
                .map(authority -> authority.getAuthority().trim().toUpperCase())
                .peek(groupName -> log.debug(String.format("Incoming group: [%s]", groupName)))
                .filter(groupName -> activeMapping.containsKey(groupName))
                .flatMap(groupName -> activeMapping.get(groupName).stream())
                .map(AcmGrantedAuthority::new)
                .collect(Collectors.toSet());
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

    public PropertyFileManager getPropertyFileManager()
    {
        return propertyFileManager;
    }

    public void setPropertyFileManager(PropertyFileManager propertyFileManager)
    {
        this.propertyFileManager = propertyFileManager;
    }

    public void setRoleToGroupMapping(AcmRoleToGroupMapping roleToGroupMapping)
    {
        this.roleToGroupMapping = roleToGroupMapping;
    }
}
