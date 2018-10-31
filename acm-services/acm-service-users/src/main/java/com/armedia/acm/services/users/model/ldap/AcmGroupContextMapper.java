package com.armedia.acm.services.users.model.ldap;

/*-
 * #%L
 * ACM Service: Users
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

import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.DefaultIncrementalAttributesMapper;

import javax.naming.directory.Attribute;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class AcmGroupContextMapper implements ContextMapper
{
    private AcmLdapSyncConfig acmLdapSyncConfig;
    private LdapTemplate template;

    public AcmGroupContextMapper(AcmLdapSyncConfig acmLdapSyncConfig, LdapTemplate template)
    {
        this.acmLdapSyncConfig = acmLdapSyncConfig;
        this.template = template;
    }

    @Override
    public LdapGroup mapFromContext(Object ctx)
    {
        DirContextAdapter adapter = (DirContextAdapter) ctx;

        LdapGroup group = new LdapGroup();
        String groupName = MapperUtils.getAttribute(adapter, "cn");
        // Throughout the application we use the group names in upper case only, so converting here at mapping level
        group.setName(MapperUtils.buildGroupName(groupName, acmLdapSyncConfig.getUserDomain()));

        group.setDistinguishedName(MapperUtils.appendToDn(adapter.getDn().toString(), acmLdapSyncConfig.getBaseDC()));
        group.setSortableValue(MapperUtils.getAttribute(adapter, acmLdapSyncConfig.getGroupsSortingAttribute()));
        group.setDescription(MapperUtils.getAttribute(adapter, "description"));
        group.setDirectoryName(acmLdapSyncConfig.getDirectoryName());
        group.setDisplayName(MapperUtils.getAttribute(adapter, "displayName"));

        Set<String> members = mapMembers(adapter, template, acmLdapSyncConfig);
        group.setMembers(members);
        return group;
    }

    protected Set<String> mapMembers(DirContextAdapter adapter, LdapTemplate template, AcmLdapSyncConfig acmLdapSyncConfig)
    {
        if (adapter.attributeExists("member"))
        {
            // AFDP-5761 Support 'range' in member attribute for large group sizes.
            if (Directory.activedirectory.equals(Directory.valueOf(acmLdapSyncConfig.getDirectoryType()))
                    && Collections.list(adapter.getAttributes().getAll()).stream().map(Attribute::getID)
                            .anyMatch(id -> id.contains("range=")))
            {
                // Incrementally retrieve all members from large groups
                List<String> members = DefaultIncrementalAttributesMapper.lookupAttributeValues(template, adapter.getDn(), "member");
                return members.stream()
                        .map(DistinguishedName::new)
                        .map(DistinguishedName::toString)
                        .collect(Collectors.toSet());
            }

            String[] members = adapter.getStringAttributes("member");

            return Arrays.stream(members)
                    .map(DistinguishedName::new)
                    .map(DistinguishedName::toString)
                    .collect(Collectors.toSet());

        }

        return Collections.emptySet();

    }
}
