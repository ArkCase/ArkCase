
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

import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.mule.api.context.MuleContextAware;
import org.mule.api.lifecycle.Disposable;
import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.Startable;
import org.mule.api.lifecycle.Stoppable;
import org.mule.module.cmis.adapters.CMISCloudConnectorConnectionIdentifierAdapter;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Generated;

@Generated(value = "Mule DevKit Version 3.4.0", date = "2014-05-13T04:20:32-03:00", comments = "Build 3.4.0.1555.8df15c1")
public class CMISCloudConnectorConnectionFactory implements KeyedPoolableObjectFactory
{

    private static Logger logger = LogManager.getLogger(CMISCloudConnectorConnectionFactory.class);
    private CMISCloudConnectorConnectionManager connectionManager;

    public CMISCloudConnectorConnectionFactory(CMISCloudConnectorConnectionManager connectionManager)
    {
        this.connectionManager = connectionManager;
    }

    public Object makeObject(Object key)
            throws Exception
    {
        if (!(key instanceof CMISCloudConnectorConnectionKey))
        {
            if (key == null)
            {
                logger.warn("Connection key is null");
            }
            else
            {
                logger.warn("Cannot cast key of type ".concat(key.getClass().getName().concat(" to ")
                        .concat("org.mule.module.cmis.connectivity.CMISCloudConnectorConnectionKey")));
            }
            throw new RuntimeException("Invalid key type ".concat(key.getClass().getName()));
        }
        CMISCloudConnectorConnectionIdentifierAdapter connector = new CMISCloudConnectorConnectionIdentifierAdapter();
        if (connector instanceof Initialisable)
        {
            ((Initialisable) connector).initialise();
        }
        if (connector instanceof MuleContextAware)
        {
            ((MuleContextAware) connector).setMuleContext(connectionManager.getMuleContext());
        }
        if (connector instanceof Startable)
        {
            ((Startable) connector).start();
        }
        if (!connector.isConnected())
        {
            connector.connect(((CMISCloudConnectorConnectionKey) key).getUsername(), ((CMISCloudConnectorConnectionKey) key).getPassword(),
                    ((CMISCloudConnectorConnectionKey) key).getBaseUrl(), ((CMISCloudConnectorConnectionKey) key).getRepositoryId(),
                    ((CMISCloudConnectorConnectionKey) key).getEndpoint(), ((CMISCloudConnectorConnectionKey) key).getConnectionTimeout(),
                    ((CMISCloudConnectorConnectionKey) key).getUseAlfrescoExtension(),
                    ((CMISCloudConnectorConnectionKey) key).getCxfPortProvider());
        }
        return connector;
    }

    public void destroyObject(Object key, Object obj)
            throws Exception
    {
        if (!(key instanceof CMISCloudConnectorConnectionKey))
        {
            if (key == null)
            {
                logger.warn("Connection key is null");
            }
            else
            {
                logger.warn("Cannot cast key of type ".concat(key.getClass().getName().concat(" to ")
                        .concat("org.mule.module.cmis.connectivity.CMISCloudConnectorConnectionKey")));
            }
            throw new RuntimeException("Invalid key type ".concat(key.getClass().getName()));
        }
        if (!(obj instanceof CMISCloudConnectorConnectionIdentifierAdapter))
        {
            if (obj == null)
            {
                logger.warn("Connector is null");
            }
            else
            {
                logger.warn("Cannot cast connector of type ".concat(obj.getClass().getName().concat(" to ")
                        .concat("org.mule.module.cmis.adapters.CMISCloudConnectorConnectionIdentifierAdapter")));
            }
            throw new RuntimeException("Invalid connector type ".concat(obj.getClass().getName()));
        }
        try
        {
            ((CMISCloudConnectorConnectionIdentifierAdapter) obj).disconnect();
        }
        catch (Exception e)
        {
            throw e;
        }
        finally
        {
            if (((CMISCloudConnectorConnectionIdentifierAdapter) obj) instanceof Stoppable)
            {
                ((Stoppable) obj).stop();
            }
            if (((CMISCloudConnectorConnectionIdentifierAdapter) obj) instanceof Disposable)
            {
                ((Disposable) obj).dispose();
            }
        }
    }

    public boolean validateObject(Object key, Object obj)
    {
        if (!(obj instanceof CMISCloudConnectorConnectionIdentifierAdapter))
        {
            if (obj == null)
            {
                logger.warn("Connector is null");
            }
            else
            {
                logger.warn("Cannot cast connector of type ".concat(obj.getClass().getName().concat(" to ")
                        .concat("org.mule.module.cmis.adapters.CMISCloudConnectorConnectionIdentifierAdapter")));
            }
            throw new RuntimeException("Invalid connector type ".concat(obj.getClass().getName()));
        }
        try
        {
            return ((CMISCloudConnectorConnectionIdentifierAdapter) obj).isConnected();
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    public void activateObject(Object key, Object obj)
            throws Exception
    {
        if (!(key instanceof CMISCloudConnectorConnectionKey))
        {
            throw new RuntimeException("Invalid key type");
        }
        if (!(obj instanceof CMISCloudConnectorConnectionIdentifierAdapter))
        {
            throw new RuntimeException("Invalid connector type");
        }
        try
        {
            if (!((CMISCloudConnectorConnectionIdentifierAdapter) obj).isConnected())
            {
                ((CMISCloudConnectorConnectionIdentifierAdapter) obj).connect(((CMISCloudConnectorConnectionKey) key).getUsername(),
                        ((CMISCloudConnectorConnectionKey) key).getPassword(), ((CMISCloudConnectorConnectionKey) key).getBaseUrl(),
                        ((CMISCloudConnectorConnectionKey) key).getRepositoryId(), ((CMISCloudConnectorConnectionKey) key).getEndpoint(),
                        ((CMISCloudConnectorConnectionKey) key).getConnectionTimeout(),
                        ((CMISCloudConnectorConnectionKey) key).getUseAlfrescoExtension(),
                        ((CMISCloudConnectorConnectionKey) key).getCxfPortProvider());
            }
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    public void passivateObject(Object key, Object obj)
            throws Exception
    {
    }

}
