
package org.mule.module.cmis.adapters;

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

import org.mule.api.MuleException;
import org.mule.api.lifecycle.Disposable;
import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.lifecycle.Startable;
import org.mule.api.lifecycle.Stoppable;
import org.mule.config.MuleManifest;
import org.mule.module.cmis.CMISCloudConnector;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Generated;

/**
 * A <code>CMISCloudConnectorLifecycleAdapter</code> is a wrapper around {@link CMISCloudConnector } that adds lifecycle
 * methods to the pojo.
 * 
 */
@Generated(value = "Mule DevKit Version 3.4.0", date = "2014-05-13T04:20:32-03:00", comments = "Build 3.4.0.1555.8df15c1")
public class CMISCloudConnectorLifecycleAdapter
        extends CMISCloudConnectorMetadataAdapater
        implements Disposable, Initialisable, Startable, Stoppable
{

    @Override
    public void start()
            throws MuleException
    {
    }

    @Override
    public void stop()
            throws MuleException
    {
    }

    @Override
    public void initialise()
            throws InitialisationException
    {
        Logger log = LogManager.getLogger(CMISCloudConnectorLifecycleAdapter.class);
        String runtimeVersion = MuleManifest.getProductVersion();
        if (runtimeVersion.equals("Unknown"))
        {
            log.warn("Unknown Mule runtime version. This module may not work properly!");
        }
        else
        {
            String[] expectedMinVersion = "3.4".split("\\.");
            if (runtimeVersion.contains("-"))
            {
                runtimeVersion = runtimeVersion.split("-")[0];
            }
            String[] currentRuntimeVersion = runtimeVersion.split("\\.");
            for (int i = 0; (i < expectedMinVersion.length); i++)
            {
                try
                {
                    if (Integer.parseInt(currentRuntimeVersion[i]) > Integer.parseInt(expectedMinVersion[i]))
                    {
                        break;
                    }
                    if (Integer.parseInt(currentRuntimeVersion[i]) < Integer.parseInt(expectedMinVersion[i]))
                    {
                        throw new RuntimeException("This module requires at least Mule 3.4");
                    }
                }
                catch (NumberFormatException nfe)
                {
                    log.warn("Error parsing Mule version, cannot validate current Mule version");
                }
                catch (ArrayIndexOutOfBoundsException iobe)
                {
                    log.warn("Error parsing Mule version, cannot validate current Mule version");
                }
            }
        }
    }

    @Override
    public void dispose()
    {
    }

}
