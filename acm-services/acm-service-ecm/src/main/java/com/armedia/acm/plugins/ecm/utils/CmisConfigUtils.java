package com.armedia.acm.plugins.ecm.utils;

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
     * @param configId configuration identifier
     * @return CMIS configuration reference
     */
    public CMISCloudConnectorConnectionManager getCmisConfiguration(String configId)
    {
        return muleContextManager.getMuleContext().getRegistry().lookupObject(configId);
    }

    /**
     * Retrieve versioning state value (used with versioningState attribute of Mule flows) for given configuration id.
     *
     * @param configId configuration identifier
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
