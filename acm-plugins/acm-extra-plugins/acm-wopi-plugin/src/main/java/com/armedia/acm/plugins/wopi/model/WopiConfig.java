package com.armedia.acm.plugins.wopi.model;

/*-
 * #%L
 * ACM Service: Wopi service
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

import org.springframework.beans.factory.annotation.Value;

public class WopiConfig
{
    @Value("${wopi.plugin.host.url}")
    private String wopiHostUrl;

    @Value("${wopi.plugin.host.validation.url}")
    private String wopiHostValidationUrl;

    @Value("${wopi.plugin.tenant.domain}")
    private String wopiTenantDomain;

    @Value("${wopi.plugin.tenant.protocol}")
    private String wopiTenantProtocol;

    @Value("${wopi.plugin.tenant.port}")
    private Integer wopiTenantPort;

    @Value("${wopi.plugin.tenant.context}")
    private String wopiTenantContext;

    @Value("${wopi.plugin.tenant.accessTokenParamName}")
    private String wopiTenantAccessTokenParamName;

    @Value("${wopi.plugin.tenant.fileIdParamName}")
    private String wopiTenantFileIdParamName;

    @Value("${wopi.plugin.lockDuration}")
    private Long wopiLockDuration;

    @Value("${wopi.plugin.enabled}")
    private Boolean wopiPluginEnabled;

    public String getWopiHostUrl(Long fileId, String accessToken)
    {
        return String.format(wopiHostUrl, fileId, accessToken, wopiTenantProtocol, wopiTenantDomain, wopiTenantPort,
                wopiTenantContext, wopiTenantAccessTokenParamName, wopiTenantFileIdParamName);
    }

    public String getWopiHostValidationUrl(Long fileId, String accessToken)
    {
        return String.format(wopiHostValidationUrl, fileId, accessToken, wopiTenantProtocol, wopiTenantDomain, wopiTenantPort,
                wopiTenantContext, wopiTenantAccessTokenParamName, wopiTenantFileIdParamName);
    }

    public String getWopiTenantDomain()
    {
        return wopiTenantDomain;
    }

    public void setWopiTenantDomain(String wopiTenantDomain)
    {
        this.wopiTenantDomain = wopiTenantDomain;
    }

    public String getWopiTenantProtocol()
    {
        return wopiTenantProtocol;
    }

    public void setWopiTenantProtocol(String wopiTenantProtocol)
    {
        this.wopiTenantProtocol = wopiTenantProtocol;
    }

    public Integer getWopiTenantPort()
    {
        return wopiTenantPort;
    }

    public void setWopiTenantPort(Integer wopiTenantPort)
    {
        this.wopiTenantPort = wopiTenantPort;
    }

    public String getWopiTenantContext()
    {
        return wopiTenantContext;
    }

    public void setWopiTenantContext(String wopiTenantContext)
    {
        this.wopiTenantContext = wopiTenantContext;
    }

    public String getWopiTenantAccessTokenParamName()
    {
        return wopiTenantAccessTokenParamName;
    }

    public void setWopiTenantAccessTokenParamName(String wopiTenantAccessTokenParamName)
    {
        this.wopiTenantAccessTokenParamName = wopiTenantAccessTokenParamName;
    }

    public String getWopiTenantFileIdParamName()
    {
        return wopiTenantFileIdParamName;
    }

    public void setWopiTenantFileIdParamName(String wopiTenantFileIdParamName)
    {
        this.wopiTenantFileIdParamName = wopiTenantFileIdParamName;
    }

    public Long getWopiLockDuration()
    {
        return wopiLockDuration;
    }

    public void setWopiLockDuration(Long wopiLockDuration)
    {
        this.wopiLockDuration = wopiLockDuration;
    }

    public Boolean getWopiPluginEnabled()
    {
        return wopiPluginEnabled;
    }

    public void setWopiPluginEnabled(Boolean wopiPluginEnabled)
    {
        this.wopiPluginEnabled = wopiPluginEnabled;
    }
}
