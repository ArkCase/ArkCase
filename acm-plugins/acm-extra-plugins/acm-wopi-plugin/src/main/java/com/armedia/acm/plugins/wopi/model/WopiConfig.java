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

public class WopiConfig
{
    private String wopiHostUrl;
    private String wopiHostValidationUrl;
    private String wopiTenantDomain;
    private String wopiTenantProtocol;
    private Long wopiTenantPort;
    private String wopiTenantContext;
    private String wopiTenantAccessTokenParamName;
    private String wopiTenantFileIdParamName;
    private Long wopiLockDuration;

    public String getWopiHostUrl(Long fileId, String accessToken)
    {
        return String.format(wopiHostUrl, fileId, accessToken, wopiTenantProtocol, "arkcase-host", 8843,
                wopiTenantContext, wopiTenantAccessTokenParamName, wopiTenantFileIdParamName);
    }

    public void setWopiHostUrl(String wopiHostUrl)
    {
        this.wopiHostUrl = wopiHostUrl;
    }

    public String getWopiHostValidationUrl(Long fileId, String accessToken)
    {
        return String.format(wopiHostValidationUrl, fileId, accessToken, wopiTenantProtocol, wopiTenantDomain, wopiTenantPort,
                wopiTenantContext, wopiTenantAccessTokenParamName, wopiTenantFileIdParamName);
    }

    public void setWopiHostValidationUrl(String wopiHostValidationUrl)
    {
        this.wopiHostValidationUrl = wopiHostValidationUrl;
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

    public Long getWopiTenantPort()
    {
        return wopiTenantPort;
    }

    public void setWopiTenantPort(Long wopiTenantPort)
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
}
