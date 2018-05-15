
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

import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.mule.api.ConnectionException;
import org.mule.api.ConnectionExceptionCode;
import org.mule.api.MuleContext;
import org.mule.api.config.MuleProperties;
import org.mule.api.construct.FlowConstruct;
import org.mule.api.context.MuleContextAware;
import org.mule.api.lifecycle.Initialisable;
import org.mule.api.retry.RetryPolicyTemplate;
import org.mule.common.DefaultResult;
import org.mule.common.DefaultTestResult;
import org.mule.common.FailureType;
import org.mule.common.TestResult;
import org.mule.common.Testable;
import org.mule.config.PoolingProfile;
import org.mule.module.cmis.CMISCloudConnector;
import org.mule.module.cmis.adapters.CMISCloudConnectorConnectionIdentifierAdapter;
import org.mule.module.cmis.basic.Capabilities;
import org.mule.module.cmis.basic.Capability;
import org.mule.module.cmis.basic.MetadataAware;
import org.mule.module.cmis.connection.ConnectionManager;
import org.mule.module.cmis.process.ProcessAdapter;
import org.mule.module.cmis.process.ProcessTemplate;

import javax.annotation.Generated;

/**
 * A {@code CMISCloudConnectorConnectionManager} is a wrapper around {@link CMISCloudConnector } that adds connection
 * management capabilities to the pojo.
 * 
 */
@Generated(value = "Mule DevKit Version 3.4.0", date = "2014-05-13T04:20:32-03:00", comments = "Build 3.4.0.1555.8df15c1")
public class CMISCloudConnectorConnectionManager implements MuleContextAware, Initialisable, Testable, Capabilities, MetadataAware,
        ConnectionManager<CMISCloudConnectorConnectionKey, CMISCloudConnectorConnectionIdentifierAdapter>,
        ProcessAdapter<CMISCloudConnectorConnectionIdentifierAdapter>
{

    private final static String MODULE_NAME = "CMIS";
    private final static String MODULE_VERSION = "1.14.1";
    private final static String DEVKIT_VERSION = "3.4.0";
    private final static String DEVKIT_BUILD = "3.4.0.1555.8df15c1";
    /**
     * Mule Context
     *
     */
    protected MuleContext muleContext;
    /**
     * Flow Construct
     *
     */
    protected FlowConstruct flowConstruct;
    protected PoolingProfile connectionPoolingProfile;
    protected RetryPolicyTemplate retryPolicyTemplate;
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
    /**
     * Connector Pool
     *
     */
    private GenericKeyedObjectPool connectionPool;

    /**
     * Retrieves muleContext
     *
     */
    public MuleContext getMuleContext()
    {
        return this.muleContext;
    }

    /**
     * Sets muleContext
     *
     * @param value
     *            Value to set
     */
    public void setMuleContext(MuleContext value)
    {
        this.muleContext = value;
    }

    /**
     * Retrieves flowConstruct
     *
     */
    public FlowConstruct getFlowConstruct()
    {
        return this.flowConstruct;
    }

    /**
     * Sets flowConstruct
     *
     * @param value
     *            Value to set
     */
    public void setFlowConstruct(FlowConstruct value)
    {
        this.flowConstruct = value;
    }

    /**
     * Retrieves connectionPoolingProfile
     *
     */
    public PoolingProfile getConnectionPoolingProfile()
    {
        return this.connectionPoolingProfile;
    }

    /**
     * Sets connectionPoolingProfile
     *
     * @param value
     *            Value to set
     */
    public void setConnectionPoolingProfile(PoolingProfile value)
    {
        this.connectionPoolingProfile = value;
    }

    /**
     * Retrieves retryPolicyTemplate
     *
     */
    public RetryPolicyTemplate getRetryPolicyTemplate()
    {
        return this.retryPolicyTemplate;
    }

    /**
     * Sets retryPolicyTemplate
     *
     * @param value
     *            Value to set
     */
    public void setRetryPolicyTemplate(RetryPolicyTemplate value)
    {
        this.retryPolicyTemplate = value;
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

    public void initialise()
    {
        GenericKeyedObjectPool.Config config = new GenericKeyedObjectPool.Config();
        if (connectionPoolingProfile != null)
        {
            config.maxIdle = connectionPoolingProfile.getMaxIdle();
            config.maxActive = connectionPoolingProfile.getMaxActive();
            config.maxWait = connectionPoolingProfile.getMaxWait();
            config.whenExhaustedAction = ((byte) connectionPoolingProfile.getExhaustedAction());
            config.timeBetweenEvictionRunsMillis = connectionPoolingProfile.getEvictionCheckIntervalMillis();
            config.minEvictableIdleTimeMillis = connectionPoolingProfile.getMinEvictionMillis();
        }
        connectionPool = new GenericKeyedObjectPool(new CMISCloudConnectorConnectionFactory(this), config);
        if (retryPolicyTemplate == null)
        {
            retryPolicyTemplate = muleContext.getRegistry().lookupObject(MuleProperties.OBJECT_DEFAULT_RETRY_POLICY_TEMPLATE);
        }
    }

    public CMISCloudConnectorConnectionIdentifierAdapter acquireConnection(CMISCloudConnectorConnectionKey key)
            throws Exception
    {
        return ((CMISCloudConnectorConnectionIdentifierAdapter) connectionPool.borrowObject(key));
    }

    public void releaseConnection(CMISCloudConnectorConnectionKey key, CMISCloudConnectorConnectionIdentifierAdapter connection)
            throws Exception
    {
        connectionPool.returnObject(key, connection);
    }

    public void destroyConnection(CMISCloudConnectorConnectionKey key, CMISCloudConnectorConnectionIdentifierAdapter connection)
            throws Exception
    {
        connectionPool.invalidateObject(key, connection);
    }

    /**
     * Returns true if this module implements such capability
     * 
     */
    public boolean isCapableOf(Capability capability)
    {
        if (capability == Capability.LIFECYCLE_CAPABLE)
        {
            return true;
        }
        if (capability == Capability.CONNECTION_MANAGEMENT_CAPABLE)
        {
            return true;
        }
        return false;
    }

    @Override
    public <P> ProcessTemplate<P, CMISCloudConnectorConnectionIdentifierAdapter> getProcessTemplate()
    {
        return new ManagedConnectionProcessTemplate(this, muleContext);
    }

    public CMISCloudConnectorConnectionKey getDefaultConnectionKey()
    {
        return new CMISCloudConnectorConnectionKey(getUsername(), getPassword(), getBaseUrl(), getRepositoryId(), getEndpoint(),
                getConnectionTimeout(), getUseAlfrescoExtension(), getCxfPortProvider());
    }

    public String getModuleName()
    {
        return MODULE_NAME;
    }

    public String getModuleVersion()
    {
        return MODULE_VERSION;
    }

    public String getDevkitVersion()
    {
        return DEVKIT_VERSION;
    }

    public String getDevkitBuild()
    {
        return DEVKIT_BUILD;
    }

    public TestResult test()
    {
        CMISCloudConnectorConnectionIdentifierAdapter connection = null;
        DefaultTestResult result;
        CMISCloudConnectorConnectionKey key = getDefaultConnectionKey();
        try
        {
            connection = acquireConnection(key);
            result = new DefaultTestResult(org.mule.common.Result.Status.SUCCESS);
        }
        catch (Exception e)
        {
            try
            {
                destroyConnection(key, connection);
            }
            catch (Exception ie)
            {
            }
            result = ((DefaultTestResult) buildFailureTestResult(e));
        }
        finally
        {
            if (connection != null)
            {
                try
                {
                    releaseConnection(key, connection);
                }
                catch (Exception ie)
                {
                }
            }
        }
        return result;
    }

    public DefaultResult buildFailureTestResult(Exception exception)
    {
        DefaultTestResult result;
        if (exception instanceof ConnectionException)
        {
            ConnectionExceptionCode code = ((ConnectionException) exception).getCode();
            if (code == ConnectionExceptionCode.UNKNOWN_HOST)
            {
                result = new DefaultTestResult(org.mule.common.Result.Status.FAILURE, exception.getMessage(), FailureType.UNKNOWN_HOST,
                        exception);
            }
            else
            {
                if (code == ConnectionExceptionCode.CANNOT_REACH)
                {
                    result = new DefaultTestResult(org.mule.common.Result.Status.FAILURE, exception.getMessage(),
                            FailureType.RESOURCE_UNAVAILABLE, exception);
                }
                else
                {
                    if (code == ConnectionExceptionCode.INCORRECT_CREDENTIALS)
                    {
                        result = new DefaultTestResult(org.mule.common.Result.Status.FAILURE, exception.getMessage(),
                                FailureType.INVALID_CREDENTIALS, exception);
                    }
                    else
                    {
                        if (code == ConnectionExceptionCode.CREDENTIALS_EXPIRED)
                        {
                            result = new DefaultTestResult(org.mule.common.Result.Status.FAILURE, exception.getMessage(),
                                    FailureType.INVALID_CREDENTIALS, exception);
                        }
                        else
                        {
                            if (code == ConnectionExceptionCode.UNKNOWN)
                            {
                                result = new DefaultTestResult(org.mule.common.Result.Status.FAILURE, exception.getMessage(),
                                        FailureType.UNSPECIFIED, exception);
                            }
                            else
                            {
                                result = new DefaultTestResult(org.mule.common.Result.Status.FAILURE, exception.getMessage(),
                                        FailureType.UNSPECIFIED, exception);
                            }
                        }
                    }
                }
            }
        }
        else
        {
            result = new DefaultTestResult(org.mule.common.Result.Status.FAILURE, exception.getMessage(), FailureType.UNSPECIFIED,
                    exception);
        }
        return result;
    }

}
