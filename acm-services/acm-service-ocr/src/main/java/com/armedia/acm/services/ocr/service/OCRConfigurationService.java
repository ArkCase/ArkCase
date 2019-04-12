package com.armedia.acm.services.ocr.service;

/*-
 * #%L
 * ACM Services: Optical character recognition via Tesseract
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
import com.armedia.acm.plugins.ecm.model.EcmFileConfig;
import com.armedia.acm.services.mediaengine.model.MediaEngineConfiguration;
import com.armedia.acm.services.ocr.model.OCRConfiguration;

/**
 * Created by Vladimir Cherepnalkovski
 */
public class OCRConfigurationService
{
    private OCRConfiguration ocrConfig;
    private EcmFileConfig ecmFileConfig;

    private ConfigurationPropertyService configurationPropertyService;

    public void saveProperties(MediaEngineConfiguration ocrConfig)
    {
        configurationPropertyService.updateProperties(ocrConfig);

        ecmFileConfig.setSnowboundEnableOcr(ocrConfig.isEnabled());
        configurationPropertyService.updateProperties(ecmFileConfig);
    }

    public OCRConfiguration loadProperties()
    {
        return ocrConfig;
    }

    public OCRConfiguration getOcrConfig()
    {
        return ocrConfig;
    }

    public void setOcrConfig(OCRConfiguration ocrConfig)
    {
        this.ocrConfig = ocrConfig;
    }

    public ConfigurationPropertyService getConfigurationPropertyService()
    {
        return configurationPropertyService;
    }

    public void setConfigurationPropertyService(ConfigurationPropertyService configurationPropertyService)
    {
        this.configurationPropertyService = configurationPropertyService;
    }

    public EcmFileConfig getEcmFileConfig()
    {
        return ecmFileConfig;
    }

    public void setEcmFileConfig(EcmFileConfig ecmFileConfig)
    {
        this.ecmFileConfig = ecmFileConfig;
    }
}
