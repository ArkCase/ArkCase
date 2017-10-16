
package org.mule.module.cmis.adapters;

import javax.annotation.Generated;
import org.mule.module.cmis.CMISCloudConnector;
import org.mule.module.cmis.basic.Capabilities;
import org.mule.module.cmis.basic.Capability;


/**
 * A <code>CMISCloudConnectorCapabilitiesAdapter</code> is a wrapper around {@link CMISCloudConnector } that implements {@link org.mule.api.Capabilities} interface.
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
    public boolean isCapableOf(Capability capability) {
        if (capability == Capability.LIFECYCLE_CAPABLE) {
            return true;
        }
        if (capability == Capability.CONNECTION_MANAGEMENT_CAPABLE) {
            return true;
        }
        return false;
    }

}
