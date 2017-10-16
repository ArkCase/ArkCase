
package org.mule.module.cmis.adapters;

import javax.annotation.Generated;
import org.mule.module.cmis.CMISCloudConnector;
import org.mule.module.cmis.basic.MetadataAware;


/**
 * A <code>CMISCloudConnectorMetadataAdapater</code> is a wrapper around {@link CMISCloudConnector } that adds support for querying metadata about the extension.
 * 
 */
@Generated(value = "Mule DevKit Version 3.4.0", date = "2014-05-13T04:20:32-03:00", comments = "Build 3.4.0.1555.8df15c1")
public class CMISCloudConnectorMetadataAdapater
    extends CMISCloudConnectorCapabilitiesAdapter
    implements MetadataAware
{

    private final static String MODULE_NAME = "CMIS";
    private final static String MODULE_VERSION = "1.14.1";
    private final static String DEVKIT_VERSION = "3.4.0";
    private final static String DEVKIT_BUILD = "3.4.0.1555.8df15c1";

    public String getModuleName() {
        return MODULE_NAME;
    }

    public String getModuleVersion() {
        return MODULE_VERSION;
    }

    public String getDevkitVersion() {
        return DEVKIT_VERSION;
    }

    public String getDevkitBuild() {
        return DEVKIT_BUILD;
    }

}
