package com.armedia.acm.services.authenticationtoken.service;

import org.springframework.cache.Cache;
import org.springframework.security.core.Authentication;

import java.util.UUID;

/**
 * Created by armdev on 8/5/14.
 */
public class AuthenticationTokenService
{
    private Cache authenticationTokenCache;

    public String storeAuthentication(Authentication auth)
    {
        String key = UUID.randomUUID().toString();
        getAuthenticationTokenCache().put(key, auth);
        return key;
    }

    public Authentication retrieveAuthentication(String key) throws IllegalArgumentException
    {
        Cache.ValueWrapper found = getAuthenticationTokenCache().get(key);

        if ( found == null || found.get() == null )
        {
            throw new IllegalArgumentException("Authentication not found for key: '" + key + "'");
        }

        return (Authentication) found.get();
    }



    public Cache getAuthenticationTokenCache()
    {
        return authenticationTokenCache;
    }

    public void setAuthenticationTokenCache(Cache authenticationTokenCache)
    {
        this.authenticationTokenCache = authenticationTokenCache;
    }
}
