package com.armedia.acm.services.users.model.ldap;

/**
 * Created by armdev on 7/2/14.
 */
public class LdapGroup
{
    private String groupName;
    private String[] memberDistinguishedNames = {};

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
}
