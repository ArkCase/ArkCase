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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OAuth2ClientRegistrationConfig
{
    @Value("${oidc.registrationId}")
    private String registrationId;

    @Value("${oidc.clientId}")
    private String clientId;

    @Value("${oidc.clientSecret}")
    private String clientSecret;

    @Value("${oidc.redirectUri}")
    private String redirectUri;

    @Value("${oidc.authorizationUri}")
    private String authorizationUri;

    @Value("${oidc.tokenUri}")
    private String tokenUri;

    @Value("${oidc.issuerUri}")
    private String issuerUri;

    @Value("${oidc.userInfoUri}")
    private String userInfoUri;

    @Value("${oidc.scope}")
    private String scope;

    public String getRegistrationId()
    {
        return registrationId;
    }

    public void setRegistrationId(String registrationId)
    {
        this.registrationId = registrationId;
    }

    public String getClientId()
    {
        return clientId;
    }

    public void setClientId(String clientId)
    {
        this.clientId = clientId;
    }

    public String getClientSecret()
    {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret)
    {
        this.clientSecret = clientSecret;
    }

    public String getRedirectUri()
    {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri)
    {
        this.redirectUri = redirectUri;
    }

    public String getAuthorizationUri()
    {
        return authorizationUri;
    }

    public void setAuthorizationUri(String authorizationUri)
    {
        this.authorizationUri = authorizationUri;
    }

    public String getTokenUri()
    {
        return tokenUri;
    }

    public void setTokenUri(String tokenUri)
    {
        this.tokenUri = tokenUri;
    }

    public String getIssuerUri()
    {
        return issuerUri;
    }

    public void setIssuerUri(String issuerUri)
    {
        this.issuerUri = issuerUri;
    }

    public String getUserInfoUri()
    {
        return userInfoUri;
    }

    public void setUserInfoUri(String userInfoUri)
    {
        this.userInfoUri = userInfoUri;
    }

    public String getScope()
    {
        return scope;
    }

    public void setScope(String scope)
    {
        this.scope = scope;
    }
}
