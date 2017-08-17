package com.armedia.acm.services.users.model.ldap;

import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.group.AcmGroupStatus;
import com.armedia.acm.services.users.model.group.AcmGroupType;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class LdapGroup
{
    private String name;
    private String distinguishedName;
    private String sortableValue;
    private String description;
    private String directoryName;
    private Set<String> members = new HashSet<>();
    private Set<String> memberUsers = new HashSet<>();
    private Set<LdapGroup> memberGroups = new HashSet<>();
    private Set<LdapGroup> descendants = new HashSet<>();
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
        acmGroup.setDescription(description);
        acmGroup.setStatus(AcmGroupStatus.ACTIVE); // TODO: fix status
        acmGroup.setAscendantsList(getAscendantsAsString());
        return acmGroup;
    }

    public boolean isChanged(AcmGroup acmGroup)
    {
        return !(Objects.equals(directoryName, acmGroup.getDirectoryName()) &&
                Objects.equals(description, acmGroup.getDescription())) &&
                Objects.equals(getAscendantsAsString(), acmGroup.getAscendantsList());
    }

    public Set<String> groupAddedUserDns(Set<String> existingMembersDns)
    {
        return memberUsers.stream()
                .filter(it -> !existingMembersDns.contains(it))
                .collect(Collectors.toSet());
    }

    public Set<String> groupAddedGroupMembers(Set<String> existingMembers)
    {
        return getMemberGroupNames().stream()
                .filter(groupName -> !existingMembers.contains(groupName))
                .collect(Collectors.toSet());
    }

    public Set<String> groupRemovedGroupMembers(Set<String> existingMembers)
    {
        return existingMembers.stream()
                .filter(groupName -> !getMemberGroupNames().contains(groupName))
                .collect(Collectors.toSet());
    }

    public Set<String> groupRemovedUserDns(Set<String> existingMembersDns)
    {
        return existingMembersDns.stream()
                .filter(it -> !getMemberUsers().contains(it))
                .collect(Collectors.toSet());
    }

    public Set<String> getMemberGroupNames()
    {
        return memberGroups.stream()
                .map(LdapGroup::getName)
                .collect(Collectors.toSet());
    }

    public boolean hasUserMember(String userId)
    {
        return memberUsers.contains(userId);
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

    public Set<String> getMembers()
    {
        return members;
    }

    public void setMembers(Set<String> members)
    {
        this.members = members;
    }

    public void setMemberUsers(Set<String> memberUsers)
    {
        this.memberUsers = memberUsers;
    }

    public void addUserMember(String userDn)
    {
        getMemberUsers().add(userDn);
    }

    public Set<String> getMemberUsers()
    {
        return memberUsers;
    }

    public Set<LdapGroup> getMemberGroups()
    {
        return memberGroups;
    }

    public void setMemberGroups(Set<LdapGroup> memberGroups)
    {
        this.memberGroups = memberGroups;
    }

    public LdapGroup memberGroups(Set<LdapGroup> members)
    {
        this.memberGroups = members;
        return this;
    }

    public void addMemberGroup(LdapGroup group)
    {
        memberGroups.add(group);
    }

    public Set<LdapGroup> getDescendants()
    {
        return descendants;
    }

    public void setDescendants(Set<LdapGroup> descendants)
    {
        this.descendants = descendants;
    }

    public Set<LdapGroup> getAscendants()
    {
        return ascendants;
    }

    public String getAscendantsAsString()
    {
        return ascendants.isEmpty() ? null :
                ascendants.stream()
                        .map(LdapGroup::getName)
                        .sorted()
                        .collect(Collectors.joining(","));
    }

    public void setAscendants(Set<LdapGroup> ascendants)
    {
        this.ascendants = ascendants;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
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
