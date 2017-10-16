package com.armedia.acm.services.users.model.ldap;

public class AcmLdapAuthenticateConfig extends AcmLdapConfig
{
    private String searchBase;

    private boolean enableEditingLdapUsers;

    public String getSearchBase()
    {
        return searchBase;
    }

    public void setSearchBase(String searchBase)
    {
        this.searchBase = searchBase;
    }

    public boolean getEnableEditingLdapUsers()
    {
        return enableEditingLdapUsers;
    }

    public void setEnableEditingLdapUsers(boolean enableEditingLdapUsers)
    {
        this.enableEditingLdapUsers = enableEditingLdapUsers;
    }
}
