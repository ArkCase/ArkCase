package com.armedia.acm.services.users.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by armdev on 7/2/14.
 */
public class LdapGroup
{
    private String groupName;
    private String[] memberDistinguishedNames = {};
    private List<AcmUser> users = new ArrayList<AcmUser>();

    public String getGroupName()
    {
        return groupName;
    }

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
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
}
