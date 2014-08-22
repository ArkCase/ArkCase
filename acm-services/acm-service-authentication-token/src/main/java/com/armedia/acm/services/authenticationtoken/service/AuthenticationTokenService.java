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

    /**
     * Retrieve a token corresponding to the given Authentication.  The token can be placed in service URLs
     * like so: http://$acm_host/$acm_servlet_context/api/v1/$api_service?acm_ticket=$token.
     * <p/>
     * The token remains valid as long as service calls using the token are made at least every half hour.  After
     * 30 minutes of non-use (e.g. no service calls made with the token), the token is no longer valid.
     * @param auth
     * @return
     */
    public String getTokenForAuthentication(Authentication auth)
    {
        String key = UUID.randomUUID().toString();
        getAuthenticationTokenCache().put(key, auth);
        return key;
    }

    /**
     * Retrieve the authentication object corresponding to a token.  The token should have been previously retrieved
     * by a call to getTokenForAuthentication.
     *
     * @param key A token (received by calling getTokenForAuthentication)
     * @return The authentication provided when the token was retrieved
     * @throws IllegalArgumentException If the token is no longer valid.
     */
    public Authentication getAuthenticationForToken(String key) throws IllegalArgumentException
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
