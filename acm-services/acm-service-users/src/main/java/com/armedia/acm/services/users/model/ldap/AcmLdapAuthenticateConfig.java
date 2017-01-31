package com.armedia.acm.services.users.model.ldap;

public class AcmLdapAuthenticateConfig extends AcmLdapConfig
{
    private String searchBase;

    private boolean changePasswordExposed;

    public String getSearchBase()
    {
        return searchBase;
    }

    public void setSearchBase(String searchBase)
    {
        this.searchBase = searchBase;
    }

    public boolean getChangePasswordExposed()
    {
        return changePasswordExposed;
    }

    public void setChangePasswordExposed(boolean changePasswordExposed)
    {
        this.changePasswordExposed = changePasswordExposed;
    }
}
