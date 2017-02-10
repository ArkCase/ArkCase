package com.armedia.acm.services.users.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LdapGroup
{
    private String groupName;
    private String distinguishedName;
    private String sortableValue;
    private String[] memberDistinguishedNames = {};
    private List<AcmUser> users = new ArrayList<>();
    private Set<String> memberOfGroups;

    public String getGroupName()
    {
        return groupName;
    }

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }

    public String getDistinguishedName()
    {
        return distinguishedName;
    }

    public void setDistinguishedName(String distinguishedName)
    {
        this.distinguishedName = distinguishedName;
    }

    public String[] getMemberDistinguishedNames()
    {
        return memberDistinguishedNames;
    }

    public void setMemberDistinguishedNames(String[] memberDistinguishedNames)
    {
        this.memberDistinguishedNames = memberDistinguishedNames;
    }

    public List<AcmUser> getUsers()
    {
        return users;
    }

    public void setUsers(List<AcmUser> users)
    {
        this.users = users;
    }

    public Set<String> getMemberOfGroups()
    {
        return memberOfGroups;
    }

    public void setMemberOfGroups(Set<String> memberOfGroups)
    {
        this.memberOfGroups = memberOfGroups;
    }

    public String getSortableValue()
    {
        return sortableValue;
    }

    public void setSortableValue(String sortableValue)
    {
        this.sortableValue = sortableValue;
    }
}
