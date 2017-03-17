package com.armedia.acm.cmis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry of CMIS configurations.
 * Created by Petar Ilin <petar.ilin@armedia.com> on 04.03.2017.
 */
public class CmisConfigRegistry
{

    /**
     * Logger instance.
     */
    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Map of CMIS configurations.
     */
    private Map<String, String> cmisConfigurations = new HashMap<>();

    /**
     * Register CMIS configuration with the registry.
     * Invoked from bean configuration files ($HOME/.arkcase/acm/cmis/mule-config-*-cmis.xml)
     *
     * @param configId        unique configuration identifier
     * @param versioningState versioning state value associated with this config (NONE, MINIR, MAJOR)
     */
    public void registerCmisConfig(String configId, String versioningState)
    {
        cmisConfigurations.put(configId, versioningState);
        log.info("Configuration [{}] added with versioningState value [{}]", configId, versioningState);
    }

    /**
     * Retrieve versioningState value of provided configuration identifier.
     *
     * @return versioning state value.
     */
    public String getVersioningState(String configId)
    {
        return cmisConfigurations.get(configId);
    }
}
