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

import com.armedia.acm.services.users.model.AcmRoleToGroupMapping;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.service.group.GroupService;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AcmGrantedAuthoritiesMapper
{

    private transient final Logger log = LogManager.getLogger(getClass());

    /**
     * This mapping is the effective mapping used when a user logs in to see
     * which roles they will have.
     */
    private AcmRoleToGroupMapping roleToGroupMapping;

    private GroupService groupService;

    public void initBean()
    {
        logProperties(roleToGroupMapping.getRoleToGroupsMap());
    }

    private void logProperties(Map<String, Set<String>> activeMapping)
    {
        String message = "Role to Group Mapping Properties: \n";
        message += activeMapping.entrySet()
                .stream()
                .map(it -> String.format("%s=%s", it.getKey(), it.getValue().toString()))
                .collect(Collectors.joining("\n"));
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
                .filter(groupName -> roleToGroupMapping.getGroupToRolesMap().containsKey(groupName))
                .flatMap(groupName -> roleToGroupMapping.getGroupToRolesMap().get(groupName).stream())
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

    public void setRoleToGroupMapping(AcmRoleToGroupMapping roleToGroupMapping)
    {
        this.roleToGroupMapping = roleToGroupMapping;
    }

    public AcmRoleToGroupMapping getRoleToGroupMapping()
    {
        return roleToGroupMapping;
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
