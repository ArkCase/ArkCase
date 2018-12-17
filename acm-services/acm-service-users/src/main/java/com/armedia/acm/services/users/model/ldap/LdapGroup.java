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

import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.group.AcmGroupConstants;
import com.armedia.acm.services.users.model.group.AcmGroupStatus;
import com.armedia.acm.services.users.model.group.AcmGroupType;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LdapGroup
{
    private String name;
    private String distinguishedName;
    private String sortableValue;
    private String description;
    private String directoryName;
    private String displayName;
    private Set<String> members = new HashSet<>();
    private Set<String> memberUserDns = new HashSet<>();
    private Set<LdapGroup> memberGroups = new HashSet<>();
    private Set<LdapGroup> ascendants = new HashSet<>();

    public AcmGroup toAcmGroup()
    {
        AcmGroup acmGroup = new AcmGroup();
        acmGroup.setName(name);
        acmGroup.setType(AcmGroupType.LDAP_GROUP);
        acmGroup.setDirectoryName(directoryName);
        acmGroup.setDistinguishedName(distinguishedName);
        setAcmGroupEditableFields(acmGroup);
        return acmGroup;
    }

    public AcmGroup setAcmGroupEditableFields(AcmGroup acmGroup)
    {
        if (description != null)
        {
            acmGroup.setDescription(description);
        }
        acmGroup.setStatus(AcmGroupStatus.ACTIVE);
        if (displayName != null)
        {
            acmGroup.setDisplayName(displayName);
        }
        acmGroup.setAscendantsList(getAscendantsAsString());
        return acmGroup;
    }

    public boolean isChanged(AcmGroup acmGroup)
    {
        boolean directoryNameChanged = !Objects.equals(directoryName, acmGroup.getDirectoryName());
        boolean descriptionChanged = !Objects.equals(description, acmGroup.getDescription());
        boolean statusChanged = AcmGroupStatus.ACTIVE != acmGroup.getStatus();
        boolean ascendantsChanged = !Objects.equals(getAscendantsAsString(), acmGroup.getAscendantsList());
        return directoryNameChanged || descriptionChanged || statusChanged || ascendantsChanged;
    }

    public Set<String> getNewUserMembers(Set<String> existingUsersDns)
    {
        Predicate<String> isNew = it -> !existingUsersDns.contains(it);
        return memberUserDns.stream()
                .filter(isNew)
                .collect(Collectors.toSet());
    }

    public Set<String> groupNewGroups(Set<String> existingGroups)
    {
        Predicate<String> isNew = groupName -> !existingGroups.contains(groupName);
        return getMemberGroupNames().stream()
                .filter(isNew)
                .collect(Collectors.toSet());
    }

    public Set<String> groupRemovedGroups(Set<String> existingGroups)
    {
        Predicate<String> notInMembers = groupName -> !getMemberGroupNames().contains(groupName);
        return existingGroups.stream()
                .filter(notInMembers)
                .collect(Collectors.toSet());
    }

    public Set<String> groupRemovedUsers(Set<String> existingUsersDns)
    {
        Predicate<String> notInMembers = it -> !memberUserDns.contains(it);
        return existingUsersDns.stream()
                .filter(notInMembers)
                .collect(Collectors.toSet());
    }

    public Set<String> getMemberGroupNames()
    {
        return memberGroups.stream()
                .map(LdapGroup::getName)
                .collect(Collectors.toSet());
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDistinguishedName()
    {
        return distinguishedName;
    }

    public void setDistinguishedName(String distinguishedName)
    {
        this.distinguishedName = distinguishedName;
    }

    public String getSortableValue()
    {
        return sortableValue;
    }

    public void setSortableValue(String sortableValue)
    {
        this.sortableValue = sortableValue;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDirectoryName()
    {
        return directoryName;
    }

    public void setDirectoryName(String directoryName)
    {
        this.directoryName = directoryName;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    public Set<String> getMembers()
    {
        return members;
    }

    public void setMembers(Set<String> members)
    {
        this.members = members;
    }

    public void addUserMember(String userDn)
    {
        getMemberUserDns().add(userDn);
    }

    public Set<String> getMemberUserDns()
    {
        return memberUserDns;
    }

    public void setMemberUserDns(Set<String> memberUserDns)
    {
        this.memberUserDns = memberUserDns;
    }

    public Set<LdapGroup> getMemberGroups()
    {
        return memberGroups;
    }

    public void setMemberGroups(Set<LdapGroup> memberGroups)
    {
        this.memberGroups = memberGroups;
    }

    public void addMemberGroup(LdapGroup group)
    {
        if (!this.equals(group))
        {
            memberGroups.add(group);
        }
    }

    public String getAscendantsAsString()
    {
        return ascendants.isEmpty() ? null
                : ascendants.stream()
                        .map(LdapGroup::getName)
                        .sorted()
                        .collect(Collectors.joining(AcmGroupConstants.ASCENDANTS_STRING_DELIMITER));
    }

    public void setAscendants(Set<LdapGroup> ascendants)
    {
        this.ascendants = ascendants;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        LdapGroup ldapGroup = (LdapGroup) o;
        return Objects.equals(name, ldapGroup.name);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name);
    }

    @Override
    public String toString()
    {
        return name;
    }
}
