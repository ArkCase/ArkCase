
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
import org.mule.module.cmis.basic.MetadataAware;

import javax.annotation.Generated;

/**
 * A <code>CMISCloudConnectorMetadataAdapater</code> is a wrapper around {@link CMISCloudConnector } that adds support
 * for querying metadata about the extension.
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

}
