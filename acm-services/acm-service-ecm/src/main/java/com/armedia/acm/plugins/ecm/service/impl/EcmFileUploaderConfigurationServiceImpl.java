package com.armedia.acm.plugins.ecm.service.impl;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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
import com.armedia.acm.plugins.ecm.model.EcmFileUploaderConfig;
import com.armedia.acm.plugins.ecm.service.EcmFileUploaderConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EcmFileUploaderConfigurationServiceImpl implements EcmFileUploaderConfigurationService
{

    private Logger log = LoggerFactory.getLogger(getClass());

    private EcmFileUploaderConfig ecmFileUploaderConfig;

    private ConfigurationPropertyService configurationPropertyService;

    @Override
    public EcmFileUploaderConfig readConfiguration()
    {
        return ecmFileUploaderConfig;
    }

    @Override
    public void writeConfiguration(EcmFileUploaderConfig configuration)
    {
        configurationPropertyService.updateProperties(configuration);
    }

    public EcmFileUploaderConfig getEcmFileUploaderConfig()
    {
        return ecmFileUploaderConfig;
    }

    public void setEcmFileUploaderConfig(EcmFileUploaderConfig ecmFileUploaderConfig)
    {
        this.ecmFileUploaderConfig = ecmFileUploaderConfig;
    }

    public ConfigurationPropertyService getConfigurationPropertyService()
    {
        return configurationPropertyService;
    }

    public void setConfigurationPropertyService(ConfigurationPropertyService configurationPropertyService)
    {
        this.configurationPropertyService = configurationPropertyService;
    }
}
