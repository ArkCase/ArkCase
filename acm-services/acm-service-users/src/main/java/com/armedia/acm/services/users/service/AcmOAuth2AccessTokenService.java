package com.armedia.acm.services.users.service;

/*-
 * #%L
 * ACM Service: Users
 * %%
 * Copyright (C) 2014 - 2021 ArkCase LLC
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.armedia.acm.services.users.dao.UserAccessTokenDao;
import com.armedia.acm.services.users.dao.exception.UserAccessTokenException;
import com.armedia.acm.services.users.dao.exception.UserRemoteActionException;
import com.armedia.acm.services.users.model.OAuth2ClientRegistrationConfig;
import com.armedia.acm.services.users.model.OAuth2Credentials;
import com.armedia.acm.services.users.model.UserAccessToken;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AcmOAuth2AccessTokenService
{
    private UserAccessTokenDao userAccessTokenDao;
    private OAuth2ClientRegistrationConfig clientRegistrationConfig;
    private RestTemplate acmRestTemplate;
    private static final Logger logger = LogManager.getLogger(AcmOAuth2AccessTokenService.class);

    private OAuth2Token getOAuth2AccessToken(OAuth2Credentials oAuth2Credentials) throws UserAccessTokenException
    {
        String tokenEndpoint = oAuth2Credentials.getTokenUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("grant_type", Collections.singletonList("client_credentials"));
        String scope = String.format("%s/.default", oAuth2Credentials.getClientId());
        params.put("scope", Collections.singletonList(scope));
        params.put("client_id", Collections.singletonList(oAuth2Credentials.getClientId()));
        params.put("client_secret", Collections.singletonList(oAuth2Credentials.getClientSecret()));
        params.put("tenant", Collections.singletonList(oAuth2Credentials.getTenant()));
        HttpEntity<MultiValueMap<String, String>> nameParam = new HttpEntity<>(params, headers);
        try
        {
            ResponseEntity<OAuth2Token> response = acmRestTemplate.postForEntity(tokenEndpoint, nameParam, OAuth2Token.class);
            logger.debug("Successfully obtained access token for tenant [{}] from [{}] provider.",
                    oAuth2Credentials.getTenant(), oAuth2Credentials.getRegistrationId());
            return response.getBody();
        }
        catch (RestClientException e)
        {
            if (e instanceof HttpClientErrorException)
            {
                HttpClientErrorException error = (HttpClientErrorException) e;
                if (error.getStatusCode() == HttpStatus.UNAUTHORIZED)
                {
                    logger.error("Username or password provided are not correct. [{}]", e.getMessage());
                    throw new UserAccessTokenException("Failed to obtain access token. Credentials provided might be incorrect");
                }
                else if (error.getStatusCode() == HttpStatus.BAD_REQUEST)
                {
                    logger.error("Failed to obtain access token. [{}]", e.getMessage());
                    throw new UserAccessTokenException("Failed to obtain access token. The request was improperly constructed");
                }
            }
            logger.error("Unknown error obtaining access token [{}]", e.getMessage(), e);
            throw new UserAccessTokenException("Failed to obtain access token");
        }
    }

    public UserAccessToken getOAuth2AccessTokenForCredentials(OAuth2Credentials oAuth2Credentials) throws UserAccessTokenException
    {
        String tenant = oAuth2Credentials.getTenant();
        String provider = oAuth2Credentials.getRegistrationId();

        UserAccessToken acmAccessToken = userAccessTokenDao.getAccessTokenByTenantAndProvider(tenant, provider);

        if (acmAccessToken != null && acmAccessToken.isExpired())
        {
            userAccessTokenDao.deleteAccessTokenForTenantAndProvider(tenant, provider);
        }
        if (acmAccessToken == null || acmAccessToken.isExpired())
        {
            OAuth2Token token = getOAuth2AccessToken(oAuth2Credentials);
            UserAccessToken accessToken = new UserAccessToken();
            accessToken.setExpirationInSec(token.getExpiresIn());
            accessToken.setProvider(provider);
            accessToken.setTenant(tenant);
            accessToken.setValue(token.getAccessToken());
            accessToken.setUserIdToken(token.getIdToken());
            accessToken.setCreatedDateTime(LocalDateTime.now());
            logger.info("Saving access token [{}]", accessToken);
            return userAccessTokenDao.save(accessToken);
        }

        return acmAccessToken;
    }

    public <T> T executeAuthenticatedRemoteAction(OAuth2Credentials oAuth2Credentials, Function<UserAccessToken, T> remoteAction)
    {
        try
        {
            return remoteAction.apply(getOAuth2AccessTokenForCredentials(oAuth2Credentials));
        }
        catch (UserAccessTokenException e)
        {
            throw new UserRemoteActionException("Failed to obtain token.", e);
        }
        catch (HttpClientErrorException e)
        {
            String tenant = oAuth2Credentials.getTenant();
            String provider = oAuth2Credentials.getRegistrationId();
            userAccessTokenDao.deleteAccessTokenForTenantAndProvider(tenant, provider);

            throw new UserRemoteActionException("Failed to execute.", e);
        }
    }

    static class OAuth2Token
    {
        @JsonProperty(value = "access_token")
        private String accessToken;

        @JsonProperty(value = "id_token")
        private String idToken;

        @JsonProperty(value = "expires_in")
        private Long expiresIn;

        public String getAccessToken()
        {
            return accessToken;
        }

        public void setAccessToken(String accessToken)
        {
            this.accessToken = accessToken;
        }

        public String getIdToken()
        {
            return idToken;
        }

        public void setIdToken(String idToken)
        {
            this.idToken = idToken;
        }

        public Long getExpiresIn()
        {
            return expiresIn;
        }

        public void setExpiresIn(Long expiresIn)
        {
            this.expiresIn = expiresIn;
        }
    }

    public OAuth2ClientRegistrationConfig getClientRegistrationConfig()
    {
        return clientRegistrationConfig;
    }

    public void setClientRegistrationConfig(OAuth2ClientRegistrationConfig clientRegistrationConfig)
    {
        this.clientRegistrationConfig = clientRegistrationConfig;
    }

    public UserAccessTokenDao getUserAccessTokenDao()
    {
        return userAccessTokenDao;
    }

    public void setUserAccessTokenDao(UserAccessTokenDao userAccessTokenDao)
    {
        this.userAccessTokenDao = userAccessTokenDao;
    }

    public RestTemplate getAcmRestTemplate()
    {
        return acmRestTemplate;
    }

    public void setAcmRestTemplate(RestTemplate acmRestTemplate)
    {
        this.acmRestTemplate = acmRestTemplate;
    }
}
