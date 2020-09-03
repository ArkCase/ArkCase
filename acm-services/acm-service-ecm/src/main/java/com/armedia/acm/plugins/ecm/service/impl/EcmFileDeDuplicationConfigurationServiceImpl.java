package com.armedia.acm.plugins.ecm.service.impl;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import com.armedia.acm.configuration.service.ConfigurationPropertyService;
import com.armedia.acm.plugins.ecm.model.EcmFileDeDuplicationConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class EcmFileDeDuplicationConfigurationServiceImpl {

    private Logger log = LogManager.getLogger(getClass());

    private EcmFileDeDuplicationConfig ecmFileDeDuplicationConfig;

    private ConfigurationPropertyService configurationPropertyService;

    public void writeConfiguration(Map<String, Object> properties)
    {
        configurationPropertyService.updateProperties(properties);
    }

    public EcmFileDeDuplicationConfig getEcmFileDeDuplicationConfig() {
        return ecmFileDeDuplicationConfig;
    }

    public void setEcmFileDeDuplicationConfig(EcmFileDeDuplicationConfig ecmFileDeDuplicationConfig) {
        this.ecmFileDeDuplicationConfig = ecmFileDeDuplicationConfig;
    }

    public ConfigurationPropertyService getConfigurationPropertyService() {
        return configurationPropertyService;
    }

    public void setConfigurationPropertyService(ConfigurationPropertyService configurationPropertyService) {
        this.configurationPropertyService = configurationPropertyService;
    }
}
