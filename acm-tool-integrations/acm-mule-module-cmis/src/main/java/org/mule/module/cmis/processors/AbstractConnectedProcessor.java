
package org.mule.module.cmis.processors;

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

@Generated(value = "Mule DevKit Version 3.4.0", date = "2014-05-13T04:20:32-03:00", comments = "Build 3.4.0.1555.8df15c1")
public abstract class AbstractConnectedProcessor
        extends AbstractExpressionEvaluator
{

    protected Object username;
    protected String _usernameType;
    protected Object password;
    protected String _passwordType;
    protected Object baseUrl;
    protected String _baseUrlType;
    protected Object repositoryId;
    protected String _repositoryIdType;
    protected Object endpoint;
    protected String _endpointType;
    protected Object connectionTimeout;
    protected String _connectionTimeoutType;
    protected Object useAlfrescoExtension;
    protected String _useAlfrescoExtensionType;
    protected Object cxfPortProvider;
    protected String _cxfPortProviderType;

    /**
     * Retrieves baseUrl
     *
     */
    public Object getBaseUrl()
    {
        return this.baseUrl;
    }

    /**
     * Sets baseUrl
     *
     * @param value
     *            Value to set
     */
    public void setBaseUrl(Object value)
    {
        this.baseUrl = value;
    }

    /**
     * Retrieves username
     *
     */
    public Object getUsername()
    {
        return this.username;
    }

    /**
     * Sets username
     *
     * @param value
     *            Value to set
     */
    public void setUsername(Object value)
    {
        this.username = value;
    }

    /**
     * Retrieves connectionTimeout
     *
     */
    public Object getConnectionTimeout()
    {
        return this.connectionTimeout;
    }

    /**
     * Sets connectionTimeout
     *
     * @param value
     *            Value to set
     */
    public void setConnectionTimeout(Object value)
    {
        this.connectionTimeout = value;
    }

    /**
     * Retrieves useAlfrescoExtension
     *
     */
    public Object getUseAlfrescoExtension()
    {
        return this.useAlfrescoExtension;
    }

    /**
     * Sets useAlfrescoExtension
     *
     * @param value
     *            Value to set
     */
    public void setUseAlfrescoExtension(Object value)
    {
        this.useAlfrescoExtension = value;
    }

    /**
     * Retrieves cxfPortProvider
     *
     */
    public Object getCxfPortProvider()
    {
        return this.cxfPortProvider;
    }

    /**
     * Sets cxfPortProvider
     *
     * @param value
     *            Value to set
     */
    public void setCxfPortProvider(Object value)
    {
        this.cxfPortProvider = value;
    }

    /**
     * Retrieves repositoryId
     *
     */
    public Object getRepositoryId()
    {
        return this.repositoryId;
    }

    /**
     * Sets repositoryId
     *
     * @param value
     *            Value to set
     */
    public void setRepositoryId(Object value)
    {
        this.repositoryId = value;
    }

    /**
     * Retrieves password
     *
     */
    public Object getPassword()
    {
        return this.password;
    }

    /**
     * Sets password
     *
     * @param value
     *            Value to set
     */
    public void setPassword(Object value)
    {
        this.password = value;
    }

    /**
     * Retrieves endpoint
     *
     */
    public Object getEndpoint()
    {
        return this.endpoint;
    }

    /**
     * Sets endpoint
     *
     * @param value
     *            Value to set
     */
    public void setEndpoint(Object value)
    {
        this.endpoint = value;
    }

}
