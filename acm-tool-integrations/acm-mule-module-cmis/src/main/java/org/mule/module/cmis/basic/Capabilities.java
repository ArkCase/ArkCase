
package org.mule.module.cmis.basic;

import javax.annotation.Generated;


/**
 * This interface is implemented for every {@link org.mule.api.annotations.Module} annotated class, to dynamically query what its capabilities are.
 * 
 */
@Generated(value = "Mule DevKit Version 3.4.0", date = "2014-05-13T04:20:32-03:00", comments = "Build 3.4.0.1555.8df15c1")
public interface Capabilities {

        /**
     * Returns true if this module implements such
     * capability
     *
     * @param capability The capability to test for
     * @return True if it does, false otherwise
     */
    boolean isCapableOf(Capability capability);
}
