package com.armedia.acm.services.users.model.ldap;

import com.armedia.acm.services.users.model.AcmUser;

/**
 * Ldap User POST JSON request
 */
public class LdapUserCreateRequest
{
    private AcmUser acmUser;
    private String password;
    private String groupName;

    public AcmUser getAcmUser()
    {
        return acmUser;
    }

    public void setAcmUser(AcmUser acmUser)
    {
        this.acmUser = acmUser;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getGroupName()
    {
        return groupName;
    }

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }
}
