package com.armedia.acm.plugins.ecm.utils;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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

import com.armedia.acm.cmis.CmisConfigRegistry;
import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;

import org.mule.module.cmis.connectivity.CMISCloudConnectorConnectionManager;

/**
 * Utility class for CMIS configuration details retrieval.
 * Created by Petar Ilin <petar.ilin@armedia.com> on 06.03.2017.
 */
public class CmisConfigUtils
{

    /**
     * Mule context manager instance.
     */
    private MuleContextManager muleContextManager;

    /**
     * Retrieve CMIS configuration (used with config-ref attribute of Mule flows) for given configuration id.
     *
     * @param configId
     *            configuration identifier
     * @return CMIS configuration reference
     */
    public CMISCloudConnectorConnectionManager getCmisConfiguration(String configId)
    {
        return muleContextManager.getMuleContext().getRegistry().lookupObject(configId);
    }

    /**
     * Retrieve versioning state value (used with versioningState attribute of Mule flows) for given configuration id.
     *
     * @param configId
     *            configuration identifier
     * @return versioning state value (NONE, MINOR, MAJOR)
     */
    public String getVersioningState(String configId)
    {
        // CMIS configuration registry exists in Mule context, so we have to obtain it first
        CmisConfigRegistry cmisConfigRegistry = muleContextManager.getMuleContext().getRegistry().lookupObject("cmisConfigRegistry");
        return cmisConfigRegistry.getVersioningState(configId);
    }

    public MuleContextManager getMuleContextManager()
    {
        return muleContextManager;
    }

    public void setMuleContextManager(MuleContextManager muleContextManager)
    {
        this.muleContextManager = muleContextManager;
    }
}
