
package org.mule.module.cmis.processors;

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
