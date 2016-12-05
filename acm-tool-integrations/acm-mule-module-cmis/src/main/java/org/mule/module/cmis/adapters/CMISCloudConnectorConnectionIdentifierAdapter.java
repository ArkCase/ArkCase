
package org.mule.module.cmis.adapters;

import javax.annotation.Generated;
import org.mule.module.cmis.CMISCloudConnector;
import org.mule.module.cmis.connection.Connection;


/**
 * A <code>CMISCloudConnectorConnectionIdentifierAdapter</code> is a wrapper around {@link CMISCloudConnector } that implements {@link org.mule.devkit.dynamic.api.helper.Connection} interface.
 * 
 */
@Generated(value = "Mule DevKit Version 3.4.0", date = "2014-05-13T04:20:32-03:00", comments = "Build 3.4.0.1555.8df15c1")
public class CMISCloudConnectorConnectionIdentifierAdapter
    extends CMISCloudConnectorProcessAdapter
    implements Connection
{


    public String getConnectionIdentifier() {
        return super.getConnectionIdentifier();
    }

}
