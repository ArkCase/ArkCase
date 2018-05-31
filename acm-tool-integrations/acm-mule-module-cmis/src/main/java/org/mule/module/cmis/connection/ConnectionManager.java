
package org.mule.module.cmis.connection;

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
 * Wrapper around {@link org.mule.api.annotations.Connector} annotated class that will infuse it with connection
 * management capabilities.
 * <p/>
 * It can receive a {@link org.mule.config.PoolingProfile} which is a configuration object used to define the connection
 * pooling parameters.
 * 
 * @param <K>
 *            Connection key
 * @param <C>
 *            Actual connector object that represents a connection
 * 
 */
@Generated(value = "Mule DevKit Version 3.4.0", date = "2014-05-13T04:20:32-03:00", comments = "Build 3.4.0.1555.8df15c1")
public interface ConnectionManager<K, C extends Connection>
{

    /**
     * Retrieve the connection pooling profile
     *
     * @return PoolingProfile representing the pooling parameters
     */
    org.mule.config.PoolingProfile getConnectionPoolingProfile();

    /**
     * Set the connection pooling profile
     *
     * @param value
     *            PoolingProfile representing the pooling parameters
     */
    void setConnectionPoolingProfile(org.mule.config.PoolingProfile value);

    /**
     * Borrow a connection from the pool
     *
     * @param connectorKey
     *            Key used to borrow the connector
     * @return An existing connector, or a newly created one
     * @throws Exception
     *             If the connection cannot be created
     */
    C acquireConnection(K connectorKey) throws Exception;

    /**
     * Return a connection to the pool
     *
     * @param connectorKey
     *            Key used to borrow the connector
     * @param connector
     *            connector to be returned to the pool
     * @throws Exception
     *             If the connection cannot be returned
     */
    void releaseConnection(K connectorKey, C connector) throws Exception;

    /**
     * Destroy a connection
     *
     * @param connectorKey
     *            Key used to borrow the connector
     * @param connector
     *            Connector to be destroyed
     * @throws Exception
     *             If the connection could not be destroyed.
     */
    void destroyConnection(K connectorKey, C connector) throws Exception;

    /**
     * Retrieve the default connection key
     *
     * @return The default connection key
     */
    K getDefaultConnectionKey();

    /**
     * Retrieve the reconnection strategy used by this connection
     * manager.
     *
     * @return The reconnection strategy {@see RetryPolicyTemplate}
     */
    org.mule.api.retry.RetryPolicyTemplate getRetryPolicyTemplate();
}
