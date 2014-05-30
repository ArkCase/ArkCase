package com.armedia.acm.services.users.model.ldap;

import java.util.Map;

/**
 * Created by armdev on 5/28/14.
 */
public class AcmLdapSyncConfig
{
    private String ldapUrl;
    private String authUserDn;
    private String authUserPassword;
    private String groupSearchBase;
    private String groupSearchFilter;
    private String userIdAttributeName;
    private boolean ignorePartialResultException;
    private String referral;
    private Map<String, String> roleToGroupMap;

    public Map<String, String> getRoleToGroupMap()
    {
        return roleToGroupMap;
    }

    public void setRoleToGroupMap(Map<String, String> roleToGroupMap)
    {
        this.roleToGroupMap = roleToGroupMap;
    }

    public String getAuthUserPassword()
    {
        return authUserPassword;
    }

    public void setAuthUserPassword(String authUserPassword)
    {
        this.authUserPassword = authUserPassword;
    }

    public String getLdapUrl()
    {
        return ldapUrl;
    }

    public void setLdapUrl(String ldapUrl)
    {
        this.ldapUrl = ldapUrl;
    }

    public String getAuthUserDn()
    {
        return authUserDn;
    }

    public void setAuthUserDn(String authUserDn)
    {
        this.authUserDn = authUserDn;
    }

    public String getGroupSearchBase()
    {
        return groupSearchBase;
    }

    public void setGroupSearchBase(String groupSearchBase)
    {
        this.groupSearchBase = groupSearchBase;
    }

    public String getGroupSearchFilter()
    {
        return groupSearchFilter;
    }

    public void setGroupSearchFilter(String groupSearchFilter)
    {
        this.groupSearchFilter = groupSearchFilter;
    }

    public String getUserIdAttributeName()
    {
        return userIdAttributeName;
    }

    public void setUserIdAttributeName(String userIdAttributeName)
    {
        this.userIdAttributeName = userIdAttributeName;
    }

    public boolean isIgnorePartialResultException()
    {
        return ignorePartialResultException;
    }

    public void setIgnorePartialResultException(boolean ignorePartialResultException)
    {
        this.ignorePartialResultException = ignorePartialResultException;
    }

    public String getReferral()
    {
        return referral;
    }

    public void setReferral(String referral)
    {
        this.referral = referral;
    }
}
