package com.armedia.acm.services.users.model.ldap;

import org.springframework.ldap.core.AuthenticationSource;

/**
 * Created by armdev on 5/28/14.
 */
public class SimpleAuthenticationSource implements AuthenticationSource
{
    private String principal;
    private String credentials;

    public SimpleAuthenticationSource(String principal, String credentials)
    {
        this.principal = principal;
        this.credentials = credentials;
    }

    @Override
    public String getPrincipal()
    {
        return principal;
    }

    @Override
    public String getCredentials()
    {
        return credentials;
    }


}
