package com.armedia.acm.camelcontext.configuration;

/*-
 * #%L
 * acm-camel-context-manager
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Aug, 2019
 */
public class ArkCaseCMISConfig
{

    @JsonProperty("cmis.id")
    private String id;
    @JsonProperty("cmis.repositoryId")
    private String repositoryId;
    @JsonProperty("cmis.baseUrl")
    private String baseUrl;
    @JsonProperty("cmis.username")
    private String username;
    @JsonProperty("cmis.password")
    private String password;
    @JsonProperty("cmis.useAlfrescoExtension")
    private Boolean useAlfrescoExtension;
    @JsonProperty("cmis.endpoint")
    private String endpoint;
    @JsonProperty("cmis.maxIdle")
    private Long maxIdle;
    @JsonProperty("cmis.maxActive")
    private Long maxActive;
    @JsonProperty("cmis.maxWait")
    private Long maxWait;
    @JsonProperty("cmis.minEvictionMillis")
    private Long minEvictionMillis;
    @JsonProperty("cmis.evictionCheckIntervalMillis")
    private Long evictionCheckIntervalMillis;
    @JsonProperty("cmis.reconnectCount")
    private Long reconnectCount;
    @JsonProperty("cmis.reconnectFrequency")
    private Long reconnectFrequency;
    @JsonProperty("cmis.cmisVersioningState")
    private String cmisVersioningState;
    @JsonProperty("cmis.timeout")
    private String timeout;
    @JsonProperty("cmis.useAuthHeader")
    private String useAuthHeader;
    @JsonProperty("cmis.remoteUserHeader")
    private String remoteUserHeader;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getRepositoryId()
    {
        return repositoryId;
    }

    public void setRepositoryId(String repositoryId)
    {
        this.repositoryId = repositoryId;
    }

    public String getBaseUrl()
    {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl)
    {
        this.baseUrl = baseUrl;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public Boolean getUseAlfrescoExtension()
    {
        return useAlfrescoExtension;
    }

    public void setUseAlfrescoExtension(Boolean useAlfrescoExtension)
    {
        this.useAlfrescoExtension = useAlfrescoExtension;
    }

    public String getEndpoint()
    {
        return endpoint;
    }

    public void setEndpoint(String endpoint)
    {
        this.endpoint = endpoint;
    }

    public Long getMaxIdle()
    {
        return maxIdle;
    }

    public void setMaxIdle(Long maxIdle)
    {
        this.maxIdle = maxIdle;
    }

    public Long getMaxActive()
    {
        return maxActive;
    }

    public void setMaxActive(Long maxActive)
    {
        this.maxActive = maxActive;
    }

    public Long getMaxWait()
    {
        return maxWait;
    }

    public void setMaxWait(Long maxWait)
    {
        this.maxWait = maxWait;
    }

    public Long getMinEvictionMillis()
    {
        return minEvictionMillis;
    }

    public void setMinEvictionMillis(Long minEvictionMillis)
    {
        this.minEvictionMillis = minEvictionMillis;
    }

    public Long getEvictionCheckIntervalMillis()
    {
        return evictionCheckIntervalMillis;
    }

    public void setEvictionCheckIntervalMillis(Long evictionCheckIntervalMillis)
    {
        this.evictionCheckIntervalMillis = evictionCheckIntervalMillis;
    }

    public Long getReconnectCount()
    {
        return reconnectCount;
    }

    public void setReconnectCount(Long reconnectCount)
    {
        this.reconnectCount = reconnectCount;
    }

    public Long getReconnectFrequency()
    {
        return reconnectFrequency;
    }

    public void setReconnectFrequency(Long reconnectFrequency)
    {
        this.reconnectFrequency = reconnectFrequency;
    }

    public String getCmisVersioningState()
    {
        return cmisVersioningState;
    }

    public void setCmisVersioningState(String cmisVersioningState)
    {
        this.cmisVersioningState = cmisVersioningState;
    }

    public String getTimeout()
    {
        return timeout;
    }

    public void setTimeout(String timeout)
    {
        this.timeout = timeout;
    }

    public String getUseAuthHeader()
    {
        return useAuthHeader;
    }

    public void setUseAuthHeader(String useAuthHeader)
    {
        this.useAuthHeader = useAuthHeader;
    }

    public String getRemoteUserHeader()
    {
        return remoteUserHeader;
    }

    public void setRemoteUserHeader(String remoteUserHeader)
    {
        this.remoteUserHeader = remoteUserHeader;
    }
}
