package com.armedia.acm.auth.oidc;

/*-
 * #%L
 * ACM Service: User Login and Authentication
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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collections;

@Configuration
@Profile("oidc")
public class OAuth2LoginConfiguration
{
    private final OAuth2ClientRegistrationConfig clientRegistrationConfig;

    public OAuth2LoginConfiguration(OAuth2ClientRegistrationConfig oAuth2ClientRegistrationConfig)
    {
        this.clientRegistrationConfig = oAuth2ClientRegistrationConfig;
    }

    @Bean
    @Profile("oidc")
    public ClientRegistrationRepository clientRegistrationRepository()
    {
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId(clientRegistrationConfig.getRegistrationId())
                .clientId(clientRegistrationConfig.getClientId())
                .clientSecret(clientRegistrationConfig.getClientSecret())
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri(clientRegistrationConfig.getRedirectUri())
                .authorizationUri(clientRegistrationConfig.getAuthorizationUri())
                .tokenUri(clientRegistrationConfig.getTokenUri())
                .issuerUri(clientRegistrationConfig.getIssuerUri())
                .userInfoUri(clientRegistrationConfig.getUserInfoUri())
                .userNameAttributeName(IdTokenClaimNames.SUB)
                .jwkSetUri(clientRegistrationConfig.getJwkSetUri())
                .scope(clientRegistrationConfig.getScope())
                .build();

        return new InMemoryClientRegistrationRepository(Collections.singletonList(clientRegistration));
    }

    @Bean
    @Profile("oidc")
    public OAuth2AuthorizedClientService authorizedClientService()
    {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository());
    }

    @Bean
    @Profile("oidc")
    public AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository()
    {
        return new HttpSessionOAuth2AuthorizationRequestRepository();
    }

    @Bean
    @Profile("oidc")
    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient()
    {
        return new DefaultAuthorizationCodeTokenResponseClient();
    }

    @Bean
    @Profile("oidc")
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> acmOAuth2UserService()
    {
        return new DefaultOAuth2UserService();
    }
}
