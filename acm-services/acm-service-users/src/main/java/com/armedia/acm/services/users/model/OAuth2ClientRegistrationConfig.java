package com.armedia.acm.services.users.model;

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

public class OAuth2ClientRegistrationConfig
{
    @Value("${oidc.registrationId}")
    private String registrationId;

    @Value("${oidc.clientId}")
    private String clientId;

    @Value("${oidc.tenantId}")
    private String tenantId;

    @Value("${oidc.clientSecret}")
    private String clientSecret;

    @Value("${oidc.tenantSecret}")
    private String tenantSecret;

    @Value("${oidc.redirectUri}")
    private String redirectUri;

    @Value("${oidc.authorizationUri}")
    private String authorizationUri;

    @Value("${oidc.tokenUri}")
    private String tokenUri;

    @Value("${oidc.jwkSetUri}")
    private String jwkSetUri;

    @Value("${oidc.issuerUri}")
    private String issuerUri;

    @Value("${oidc.userInfoUri}")
    private String userInfoUri;

    @Value("${oidc.usernameAttribute}")
    private String usernameAttribute;

    @Value("#{'${oidc.scope}'.split(',')}")
    private String[] scopes;

    @Value("${oidc.systemUserEmail}")
    private String systemUserEmail;

    @Value("${oidc.systemUserPassword}")
    private String systemUserPassword;

    @Value("${oidc.usersDirectory}")
    private String usersDirectory;

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

    public String getTenantId()
    {
        return tenantId;
    }

    public void setTenantId(String tenantId)
    {
        this.tenantId = tenantId;
    }

    public String getTenantSecret()
    {
        return tenantSecret;
    }

    public void setTenantSecret(String tenantSecret)
    {
        this.tenantSecret = tenantSecret;
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

    public String getUsernameAttribute()
    {
        return usernameAttribute;
    }

    public void setUsernameAttribute(String usernameAttribute)
    {
        this.usernameAttribute = usernameAttribute;
    }

    public String getJwkSetUri()
    {
        return jwkSetUri;
    }

    public void setJwkSetUri(String jwkSetUri)
    {
        this.jwkSetUri = jwkSetUri;
    }

    public String[] getScopes()
    {
        return scopes;
    }

    public void setScopes(String[] scopes)
    {
        this.scopes = scopes;
    }

    public String getSystemUserEmail()
    {
        return systemUserEmail;
    }

    public void setSystemUserEmail(String systemUserEmail)
    {
        this.systemUserEmail = systemUserEmail;
    }

    public String getSystemUserPassword()
    {
        return systemUserPassword;
    }

    public void setSystemUserPassword(String systemUserPassword)
    {
        this.systemUserPassword = systemUserPassword;
    }

    public String getUsersDirectory()
    {
        return usersDirectory;
    }

    public void setUsersDirectory(String usersDirectory)
    {
        this.usersDirectory = usersDirectory;
    }
}
