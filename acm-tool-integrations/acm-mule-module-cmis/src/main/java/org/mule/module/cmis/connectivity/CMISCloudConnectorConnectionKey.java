
package org.mule.module.cmis.connectivity;

/*-
 * #%L
 * ACM Mule CMIS Connector
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

import javax.annotation.Generated;

/**
 * A tuple of connection parameters
 * 
 */
@Generated(value = "Mule DevKit Version 3.4.0", date = "2014-05-13T04:20:32-03:00", comments = "Build 3.4.0.1555.8df15c1")
public class CMISCloudConnectorConnectionKey
{

    /**
     * 
     */
    private String username;
    /**
     * 
     */
    private String password;
    /**
     * 
     */
    private String baseUrl;
    /**
     * 
     */
    private String repositoryId;
    /**
     * 
     */
    private String endpoint;
    /**
     * 
     */
    private String connectionTimeout;
    /**
     * 
     */
    private String useAlfrescoExtension;
    /**
     * 
     */
    private String cxfPortProvider;

    public CMISCloudConnectorConnectionKey(String username, String password, String baseUrl, String repositoryId, String endpoint,
            String connectionTimeout, String useAlfrescoExtension, String cxfPortProvider)
    {
        this.username = username;
        this.password = password;
        this.baseUrl = baseUrl;
        this.repositoryId = repositoryId;
        this.endpoint = endpoint;
        this.connectionTimeout = connectionTimeout;
        this.useAlfrescoExtension = useAlfrescoExtension;
        this.cxfPortProvider = cxfPortProvider;
    }

    /**
     * Retrieves baseUrl
     *
     */
    public String getBaseUrl()
    {
        return this.baseUrl;
    }

    /**
     * Sets baseUrl
     *
     * @param value
     *            Value to set
     */
    public void setBaseUrl(String value)
    {
        this.baseUrl = value;
    }

    /**
     * Retrieves username
     *
     */
    public String getUsername()
    {
        return this.username;
    }

    /**
     * Sets username
     *
     * @param value
     *            Value to set
     */
    public void setUsername(String value)
    {
        this.username = value;
    }

    /**
     * Retrieves connectionTimeout
     *
     */
    public String getConnectionTimeout()
    {
        return this.connectionTimeout;
    }

    /**
     * Sets connectionTimeout
     *
     * @param value
     *            Value to set
     */
    public void setConnectionTimeout(String value)
    {
        this.connectionTimeout = value;
    }

    /**
     * Retrieves useAlfrescoExtension
     *
     */
    public String getUseAlfrescoExtension()
    {
        return this.useAlfrescoExtension;
    }

    /**
     * Sets useAlfrescoExtension
     *
     * @param value
     *            Value to set
     */
    public void setUseAlfrescoExtension(String value)
    {
        this.useAlfrescoExtension = value;
    }

    /**
     * Retrieves cxfPortProvider
     *
     */
    public String getCxfPortProvider()
    {
        return this.cxfPortProvider;
    }

    /**
     * Sets cxfPortProvider
     *
     * @param value
     *            Value to set
     */
    public void setCxfPortProvider(String value)
    {
        this.cxfPortProvider = value;
    }

    /**
     * Retrieves repositoryId
     *
     */
    public String getRepositoryId()
    {
        return this.repositoryId;
    }

    /**
     * Sets repositoryId
     *
     * @param value
     *            Value to set
     */
    public void setRepositoryId(String value)
    {
        this.repositoryId = value;
    }

    /**
     * Retrieves password
     *
     */
    public String getPassword()
    {
        return this.password;
    }

    /**
     * Sets password
     *
     * @param value
     *            Value to set
     */
    public void setPassword(String value)
    {
        this.password = value;
    }

    /**
     * Retrieves endpoint
     *
     */
    public String getEndpoint()
    {
        return this.endpoint;
    }

    /**
     * Sets endpoint
     *
     * @param value
     *            Value to set
     */
    public void setEndpoint(String value)
    {
        this.endpoint = value;
    }

    public int hashCode()
    {
        int hash = 1;
        hash = (hash * 31);
        if (this.username != null)
        {
            hash += this.username.hashCode();
        }
        hash = (hash * 31);
        if (this.baseUrl != null)
        {
            hash += this.baseUrl.hashCode();
        }
        return hash;
    }

    public boolean equals(Object obj)
    {
        return (((((obj instanceof CMISCloudConnectorConnectionKey) && (this.username != null))
                && this.username.equals(((CMISCloudConnectorConnectionKey) obj).username)) && (this.baseUrl != null))
                && this.baseUrl.equals(((CMISCloudConnectorConnectionKey) obj).baseUrl));
    }

}
