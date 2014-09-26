package com.armedia.acm.services.users.model.ldap;

public class AcmLdapConfig
{
    private String ldapUrl;
    private String authUserDn;
    private String authUserPassword;
    private String userIdAttributeName;
    private boolean ignorePartialResultException;
    private String referral;

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
