package com.armedia.acm.services.users.model;

import com.armedia.acm.services.users.model.group.AcmGroup;
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
    private Set<String> memberGroups = new HashSet<>();
    private Set<String> memberUsers = new HashSet<>();
    private Set<String> parentGroups = new HashSet<>();

    public AcmGroup toAcmGroup()
    {
        AcmGroup acmGroup = new AcmGroup();
        acmGroup.setName(getName());
        acmGroup.setType(AcmGroupType.LDAP_GROUP.name());
        acmGroup.setDirectoryName(getDirectoryName());
        acmGroup.setDistinguishedName(getDistinguishedName());
        setAcmGroupEditableFields(acmGroup);
        return acmGroup;
    }

    public AcmGroup setAcmGroupEditableFields(AcmGroup acmGroup)
    {
        acmGroup.setDescription(getDescription());
        acmGroup.setStatus("ACTIVE"); // TODO: fix status
        return acmGroup;
    }

    public boolean isChanged(AcmGroup acmGroup)
    {
        return !(Objects.equals(getDirectoryName(), acmGroup.getDirectoryName()) &&
                Objects.equals(getDescription(), acmGroup.getDescription()));
    }

     public Set<String> groupAddedUserDns(Set<String> existingMembersDns)
    {
        return getMemberUsers().stream()
                .filter(it -> !existingMembersDns.contains(it))
                .collect(Collectors.toSet());
    }

    public Set<String> groupAddedGroupMembers(Set<String> existingMembers)
    {
        return getMemberGroups().stream()
                .filter(it -> !existingMembers.contains(it))
                .collect(Collectors.toSet());
    }

    public Set<String> groupRemovedGroupMembers(Set<String> existingMembers)
    {
        return existingMembers.stream()
                .filter(it -> !getMemberGroups().contains(it))
                .collect(Collectors.toSet());
    }

    public Set<String> groupRemovedUserDns(Set<String> existingMembersDns)
    {
        return existingMembersDns.stream()
                .filter(it -> !getMemberUsers().contains(it))
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

    public Set<String> getMembers()
    {
        return members;
    }

    public void setMembers(Set<String> members)
    {
        this.members = members;
    }

    public void addGroupMember(String groupName)
    {
        getMemberGroups().add(groupName);
    }

    public Set<String> getMemberGroups()
    {
        return memberGroups;
    }

    public void addUserMember(String userDn)
    {
        getMemberUsers().add(userDn);
    }

    public Set<String> getMemberUsers()
    {
        return memberUsers;
    }

    public Set<String> getParentGroups()
    {
        return parentGroups;
    }

    public void setParentGroups(Set<String> parentGroups)
    {
        this.parentGroups = parentGroups;
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
