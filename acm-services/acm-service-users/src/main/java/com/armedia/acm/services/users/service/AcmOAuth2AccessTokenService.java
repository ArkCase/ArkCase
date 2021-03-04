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

import com.armedia.acm.services.users.dao.UserAccessTokenDao;
import com.armedia.acm.services.users.dao.exception.UserAccessTokenException;
import com.armedia.acm.services.users.dao.exception.UserRemoteActionException;
import com.armedia.acm.services.users.model.OAuth2ClientRegistrationConfig;
import com.armedia.acm.services.users.model.UserAccessToken;
import com.fasterxml.jackson.annotation.JsonProperty;

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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.function.Function;

public class AcmOAuth2AccessTokenService
{
    private UserAccessTokenDao userAccessTokenDao;
    private OAuth2ClientRegistrationConfig clientRegistrationConfig;
    private RestTemplate acmRestTemplate;
    private static final Logger logger = LogManager.getLogger(AcmOAuth2AccessTokenService.class);

    private OAuth2Token getOAuth2AccessToken() throws UserAccessTokenException
    {
        String tokenEndpoint = clientRegistrationConfig.getTokenUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("grant_type", Collections.singletonList("password"));
        params.put("scope", Collections.singletonList("openid"));
        params.put("client_id", Collections.singletonList(clientRegistrationConfig.getTenantId()));
        params.put("client_secret", Collections.singletonList(clientRegistrationConfig.getTenantSecret()));
        params.put("username", Collections.singletonList(clientRegistrationConfig.getSystemUserEmail()));
        params.put("password", Collections.singletonList(clientRegistrationConfig.getSystemUserPassword()));
        HttpEntity<MultiValueMap<String, String>> nameParam = new HttpEntity<>(params, headers);
        try
        {
            ResponseEntity<OAuth2Token> response = acmRestTemplate.postForEntity(tokenEndpoint, nameParam, OAuth2Token.class);
            logger.debug("Successfully obtained access token for user [{}] from [{}] provider.",
                    clientRegistrationConfig.getSystemUserEmail(), clientRegistrationConfig.getRegistrationId());
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

    public UserAccessToken getOAuth2AccessTokenForSystemUser() throws UserAccessTokenException
    {
        String systemUserEmail = clientRegistrationConfig.getSystemUserEmail();
        String provider = clientRegistrationConfig.getRegistrationId();

        UserAccessToken acmAccessToken = userAccessTokenDao.getAccessTokenByUserAndProvider(systemUserEmail, provider);

        if (acmAccessToken != null && acmAccessToken.isExpired())
        {
            userAccessTokenDao.deleteAccessTokenForUserAndProvider(systemUserEmail, provider);
        }
        if (acmAccessToken == null || acmAccessToken.isExpired())
        {
            OAuth2Token token = getOAuth2AccessToken();
            UserAccessToken accessToken = new UserAccessToken();
            accessToken.setExpirationInSec(token.getExpiresIn());
            accessToken.setProvider(provider);
            accessToken.setUserEmail(systemUserEmail);
            // Current implementation requires the use of ID tokens as opposed to access tokens. Might need to be changed
            // in the future in accordance with the tenant and provider
            accessToken.setValue(token.getIdToken());
            accessToken.setCreatedDateTime(LocalDateTime.now());
            logger.info("Saving access token [{}]", accessToken);
            return userAccessTokenDao.save(accessToken);
        }

        return acmAccessToken;
    }

    public <T> T executeAuthenticatedRemoteAction(Function<UserAccessToken, T> remoteAction)
    {
        try
        {
            return remoteAction.apply(getOAuth2AccessTokenForSystemUser());
        }
        catch (UserAccessTokenException e)
        {
            throw new UserRemoteActionException("Failed to obtain token.", e);
        }
        catch (HttpClientErrorException e)
        {
            // remote action failed due to expired token
            // TODO: To be tested and changed accordingly
            // retry on more specific exception
            String systemUserEmail = clientRegistrationConfig.getSystemUserEmail();
            String provider = clientRegistrationConfig.getRegistrationId();
            userAccessTokenDao.deleteAccessTokenForUserAndProvider(systemUserEmail, provider);

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
