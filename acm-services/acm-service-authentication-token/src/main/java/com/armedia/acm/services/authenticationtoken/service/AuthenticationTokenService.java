package com.armedia.acm.services.authenticationtoken.service;

/*-
 * #%L
 * ACM Service: Authentication Tokens
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.services.authenticationtoken.dao.AuthenticationTokenDao;
import com.armedia.acm.services.authenticationtoken.model.AuthenticationToken;
import com.armedia.acm.services.authenticationtoken.model.AuthenticationTokenConstants;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.cache.Cache;
import org.springframework.security.core.Authentication;
import org.springframework.web.context.request.RequestContextHolder;

import java.time.temporal.TemporalAmount;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by armdev on 8/5/14.
 */
public class AuthenticationTokenService
{
    private Cache authenticationTokenCache;
    private AuthenticationTokenDao authenticationTokenDao;

    public static final int EMAIL_TICKET_EXPIRATION_DAYS = 3;
    private Logger log = LogManager.getLogger(getClass());

    /**
     * Retrieve a token corresponding to the given Authentication. The token can be placed in service URLs
     * like so: http://$acm_host/$acm_servlet_context/api/v1/$api_service?acm_ticket=$token.
     * <p/>
     * The token remains valid as long as service calls using the token are made at least every half hour. After
     * 30 minutes of non-use (e.g. no service calls made with the token), the token is no longer valid.
     * 
     * @param auth
     * @return
     */
    public String getTokenForAuthentication(Authentication auth)
    {
        String key = UUID.randomUUID().toString();
        getAuthenticationTokenCache().put(key, auth);
        storeTokenForAuthenticationPerSession(key);
        return key;
    }

    public void storeTokenForAuthenticationPerSession(String token)
    {
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        Cache.ValueWrapper valueWrapper = getAuthenticationTokenCache().get(sessionId);
        Set<String> tokens;
        if (valueWrapper != null)
        {
            tokens = (Set<String>) valueWrapper.get();
        }
        else
        {
            tokens = new HashSet<>();
        }
        tokens.add(token);
        getAuthenticationTokenCache().put(sessionId, tokens);
    }

    public String getUncachedTokenForAuthentication(Authentication auth)
    {
        String key = UUID.randomUUID().toString();
        return key;
    }

    /**
     * Retrieve the authentication object corresponding to a token. The token should have been previously retrieved
     * by a call to getTokenForAuthentication.
     *
     * @param key
     *            A token (received by calling getTokenForAuthentication)
     * @return The authentication provided when the token was retrieved
     * @throws IllegalArgumentException
     *             If the token is no longer valid.
     */
    public Authentication getAuthenticationForToken(String key) throws IllegalArgumentException
    {
        Cache.ValueWrapper found = getAuthenticationTokenCache().get(key);

        if (found == null || found.get() == null)
        {
            throw new IllegalArgumentException("Authentication not found for key: '" + key + "'");
        }

        return (Authentication) found.get();
    }

    public void purgeTokenForAuthenticationPerSession(String sessionId)
    {
        Cache.ValueWrapper valueWrapper = getAuthenticationTokenCache().get(sessionId);
        if (valueWrapper != null)
        {
            Set<String> tokens = (Set<String>) valueWrapper.get();
            tokens.forEach(
                    token -> getAuthenticationTokenCache().evict(token));
            getAuthenticationTokenCache().evict(sessionId);
        }
    }

    public String generateAndSaveAuthenticationToken(Long fileId, String emailAddress, Authentication authentication)
    {
        log.debug("Generation authentication token for email address [{}]", emailAddress);
        String token = getUncachedTokenForAuthentication(authentication);
        AuthenticationToken authenticationToken = new AuthenticationToken();
        authenticationToken.setKey(token);
        authenticationToken.setStatus(AuthenticationTokenConstants.ACTIVE);
        authenticationToken.setEmail(emailAddress);
        authenticationToken.setFileId(fileId);
        authenticationTokenDao.save(authenticationToken);
        return token;
    }

    public AuthenticationToken findByKey(String key)
    {
        return authenticationTokenDao.findAuthenticationTokenByKey(key);
    }

    /**
     * Calculates when an access token expires, by adding temporalValidityAmount to the creation time.
     * @param token
     * @param temporalValidityAmount
     * @return number of milliseconds since January 1, 1970 UTC (the date epoch in JavaScript)
     */
    public Long calculateTokenTimeToLive(AuthenticationToken token, TemporalAmount temporalValidityAmount)
    {
        return token.getCreated().toInstant()
                .plus(temporalValidityAmount)
                .toEpochMilli();
    }

    public void saveAuthenticationToken(AuthenticationToken token)
    {
        authenticationTokenDao.save(token);
    }

    public Cache getAuthenticationTokenCache()
    {
        return authenticationTokenCache;
    }

    public void setAuthenticationTokenCache(Cache authenticationTokenCache)
    {
        this.authenticationTokenCache = authenticationTokenCache;
    }

    public AuthenticationTokenDao getAuthenticationTokenDao()
    {
        return authenticationTokenDao;
    }

    public void setAuthenticationTokenDao(AuthenticationTokenDao authenticationTokenDao)
    {
        this.authenticationTokenDao = authenticationTokenDao;
    }
}
