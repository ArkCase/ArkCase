package com.armedia.acm.auth;

/*-
 * #%L
 * ACM Service: User Login and Authentication
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.files.ConfigurationFileChangedEvent;
import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.services.users.model.AcmRoleToGroupMapping;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.event.LdapGroupCreatedEvent;
import com.armedia.acm.services.users.model.event.LdapGroupDeletedEvent;

import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.service.group.GroupService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
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
import java.util.stream.Stream;

public class AcmGrantedAuthoritiesMapper implements ApplicationListener<ApplicationEvent>
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

    private GroupService groupService;

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
    public void onApplicationEvent(ApplicationEvent event)
    {
        if (event instanceof ConfigurationFileChangedEvent)
        {
            ConfigurationFileChangedEvent configurationFileChangedEvent = (ConfigurationFileChangedEvent) event;
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
        else if (event instanceof LdapGroupCreatedEvent || event instanceof LdapGroupDeletedEvent)
        {
            initBean();
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

    public Collection<AcmGrantedAuthority> getAuthorityGroups(AcmUser user)
    {
        // All LDAP and ADHOC groups that the user belongs to (all these we are keeping in the database)
        List<AcmGroup> groups = groupService.findByUserMember(user);

        Stream<AcmGrantedGroupAuthority> authorityGroups = groups.stream()
                .map(authority -> new AcmGrantedGroupAuthority(authority.getName(), authority.getIdentifier()));

        Stream<AcmGrantedGroupAuthority> authorityAscendantsGroups = groups.stream()
                .flatMap(AcmGroup::getAscendantsStream)
                .map(it -> groupService.findByName(it))
                .map(authority -> new AcmGrantedGroupAuthority(authority.getName(), authority.getIdentifier()));

        return Stream.concat(authorityGroups, authorityAscendantsGroups)
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

    public GroupService getGroupService()
    {
        return groupService;
    }

    public void setGroupService(GroupService groupService)
    {
        this.groupService = groupService;
    }
}
