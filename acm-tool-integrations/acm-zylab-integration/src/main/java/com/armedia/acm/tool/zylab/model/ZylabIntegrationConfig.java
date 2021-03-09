package com.armedia.acm.tool.zylab.model;

/*-
 * #%L
 * Tool Integrations: Arkcase ZyLAB Integration
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

import com.armedia.acm.services.users.model.OAuth2Credentials;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by Aleksandar Acevski <aleksandar.acevski@armedia.com> on January, 2021
 */
@JsonSerialize(as = ZylabIntegrationConfig.class)
public class ZylabIntegrationConfig
{

    @JsonProperty("zylabIntegration.enabled")
    @Value("${zylabIntegration.enabled}")
    private Boolean enabled;

    @JsonProperty("zylabIntegration.defaultMatterTemplateId")
    @Value("${zylabIntegration.defaultMatterTemplateId}")
    private Long defaultMatterTemplateId;

    @JsonProperty("zylabIntegration.host")
    @Value("${zylabIntegration.host}")
    private String host;

    @JsonProperty("zylabIntegration.port")
    @Value("${zylabIntegration.port}")
    private Integer port;

    @JsonProperty("zylabIntegration.basePath")
    @Value("${zylabIntegration.basePath}")
    private String basePath;

    @JsonProperty("zylabIntegration.url")
    private String baseUrl;

    @JsonProperty("zylabIntegration.simpleResourcePath")
    @Value("${zylabIntegration.simpleResourcePath}")
    private String simpleResourcePath;

    @JsonProperty("zylabIntegration.documentReviewPath")
    @Value("${zylabIntegration.documentReviewPath}")
    private String documentReviewPath;

    @JsonProperty("zylabIntegration.matterDashboardPath")
    @Value("${zylabIntegration.matterDashboardPath}")
    private String matterDashboardPath;

    @JsonProperty("zylabIntegration.createMatterPath")
    @Value("${zylabIntegration.createMatterPath}")
    private String createMatterPath;

    @JsonProperty("zylabIntegration.downloadProductionPath")
    @Value("${zylabIntegration.downloadProductionPath}")
    private String downloadProductionPath;

    @JsonProperty("zylabIntegration.openMatterPath")
    @Value("${zylabIntegration.openMatterPath}")
    private String openMatterPath;

    @JsonProperty("zylabIntegration.matterReportsPath")
    @Value("${zylabIntegration.matterReportsPath}")
    private String matterReportsPath;

    @JsonProperty("zylabIntegration.getMatterTemplatesPath")
    @Value("${zylabIntegration.getMatterTemplatesPath}")
    private String getMatterTemplatesPath;

    @JsonProperty("zylabIntegration.viewDocumentPath")
    @Value("${zylabIntegration.viewDocumentPath}")
    private String viewDocumentPath;

    @JsonIgnore
    @Value("${zylabIntegration.authentication.registrationId}")
    private String registrationId;

    @JsonIgnore
    @Value("${zylabIntegration.authentication.clientId}")
    private String clientId;

    @JsonIgnore
    @Value("${zylabIntegration.authentication.clientSecret}")
    private String clientSecret;

    @JsonIgnore
    @Value("${zylabIntegration.authentication.tokenUri}")
    private String tokenUri;

    @JsonIgnore
    @Value("${zylabIntegration.authentication.systemUserEmail}")
    private String systemUserEmail;

    @JsonIgnore
    @Value("${zylabIntegration.authentication.systemUserPassword}")
    private String systemUserPassword;

    @JsonIgnore
    private OAuth2Credentials oAuth2Credentials = new OAuth2Credentials();

    public Boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(Boolean enabled)
    {
        this.enabled = enabled;
    }

    public Long getDefaultMatterTemplateId()
    {
        return defaultMatterTemplateId;
    }

    public void setDefaultMatterTemplateId(Long defaultMatterTemplateId)
    {
        this.defaultMatterTemplateId = defaultMatterTemplateId;
    }

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public Integer getPort()
    {
        return port;
    }

    public void setPort(Integer port)
    {
        this.port = port;
    }

    public String getBasePath()
    {
        return basePath;
    }

    public void setBasePath(String basePath)
    {
        this.basePath = basePath;
    }

    public String getBaseUrl()
    {
        return new StringBuilder().append("https://").append(host).append(":").append(port).append(basePath).toString();
    }

    public void setBaseUrl(String baseUrl)
    {
        this.baseUrl = baseUrl;
    }

    public String getSimpleResourcePath()
    {
        return simpleResourcePath;
    }

    public void setSimpleResourcePath(String simpleResourcePath)
    {
        this.simpleResourcePath = simpleResourcePath;
    }

    public String getDocumentReviewPath()
    {
        return documentReviewPath;
    }

    public void setDocumentReviewPath(String documentReviewPath)
    {
        this.documentReviewPath = documentReviewPath;
    }

    public String getMatterDashboardPath()
    {
        return matterDashboardPath;
    }

    public void setMatterDashboardPath(String matterDashboardPath)
    {
        this.matterDashboardPath = matterDashboardPath;
    }

    public String getCreateMatterPath()
    {
        return createMatterPath;
    }

    public void setCreateMatterPath(String createMatterPath)
    {
        this.createMatterPath = createMatterPath;
    }

    public String getDownloadProductionPath()
    {
        return downloadProductionPath;
    }

    public void setDownloadProductionPath(String downloadProductionPath)
    {
        this.downloadProductionPath = downloadProductionPath;
    }

    public String getOpenMatterPath()
    {
        return openMatterPath;
    }

    public void setOpenMatterPath(String openMatterPath)
    {
        this.openMatterPath = openMatterPath;
    }

    public String getMatterReportsPath()
    {
        return matterReportsPath;
    }

    public void setMatterReportsPath(String matterReportsPath)
    {
        this.matterReportsPath = matterReportsPath;
    }

    public String getViewDocumentPath()
    {
        return viewDocumentPath;
    }

    public void setViewDocumentPath(String viewDocumentPath)
    {
        this.viewDocumentPath = viewDocumentPath;
    }

    public String getGetMatterTemplatesPath()
    {
        return getMatterTemplatesPath;
    }

    public void setGetMatterTemplatesPath(String getMatterTemplatesPath)
    {
        this.getMatterTemplatesPath = getMatterTemplatesPath;
    }

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

    public String getTokenUri()
    {
        return tokenUri;
    }

    public void setTokenUri(String tokenUri)
    {
        this.tokenUri = tokenUri;
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

    @JsonIgnore
    public OAuth2Credentials getoAuth2Credentials()
    {
        oAuth2Credentials.setRegistrationId(registrationId);
        oAuth2Credentials.setClientId(clientId);
        oAuth2Credentials.setClientSecret(clientSecret);
        oAuth2Credentials.setTokenUri(tokenUri);
        oAuth2Credentials.setSystemUserEmail(systemUserEmail);
        oAuth2Credentials.setSystemUserPassword(systemUserPassword);

        return oAuth2Credentials;
    }
}
