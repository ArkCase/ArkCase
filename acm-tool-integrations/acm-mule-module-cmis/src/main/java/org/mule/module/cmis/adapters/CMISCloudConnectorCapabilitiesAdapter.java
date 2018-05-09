
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

import org.mule.module.cmis.CMISCloudConnector;
import org.mule.module.cmis.basic.Capabilities;
import org.mule.module.cmis.basic.Capability;

import javax.annotation.Generated;

/**
 * A <code>CMISCloudConnectorCapabilitiesAdapter</code> is a wrapper around {@link CMISCloudConnector } that implements
 * {@link org.mule.api.Capabilities} interface.
 * 
 */
@Generated(value = "Mule DevKit Version 3.4.0", date = "2014-05-13T04:20:32-03:00", comments = "Build 3.4.0.1555.8df15c1")
public class CMISCloudConnectorCapabilitiesAdapter
        extends CMISCloudConnector
        implements Capabilities
{

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

}
