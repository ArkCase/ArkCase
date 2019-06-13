package com.armedia.acm.cmis;

/*-
 * #%L
 * Tool Integrations: CMIS Configuration
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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

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
    private Logger log = LogManager.getLogger(getClass());

    /**
     * Map of CMIS configurations.
     */
    private Map<String, String> cmisConfigurations = new HashMap<>();

    /**
     * Register CMIS configuration with the registry.
     * Invoked from bean configuration files ($HOME/.arkcase/acm/cmis/mule-config-*-cmis.xml)
     *
     * @param configId
     *            unique configuration identifier
     * @param versioningState
     *            versioning state value associated with this config (NONE, MINIR, MAJOR)
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
