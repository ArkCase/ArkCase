package com.armedia.acm.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class AcmAuthentication implements Authentication
{
    private final Collection<AcmGrantedAuthority> authorities;
    private final Object credentials;
    private final Object details;
    private final Object principal;
    private boolean authenticated;
    private final String name;

    public AcmAuthentication(Collection<AcmGrantedAuthority> authorities,
                             Object credentials,
                             Object details,
                             boolean authenticated,
                             String userId)
    {
        this.authorities = authorities;
        this.credentials = credentials;
        this.details = details;
        this.principal = userId;
        this.authenticated = authenticated;
        this.name = userId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return authorities;
    }

    @Override
    public Object getCredentials()
    {
        return credentials;
    }

    @Override
    public Object getDetails()
    {
        return details;
    }

    @Override
    public Object getPrincipal()
    {
        return principal;
    }

    @Override
    public boolean isAuthenticated()
    {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean authenticated) throws IllegalArgumentException
    {
        this.authenticated = authenticated;
    }

    @Override
    public String getName()
    {
        return name;
    }
}
