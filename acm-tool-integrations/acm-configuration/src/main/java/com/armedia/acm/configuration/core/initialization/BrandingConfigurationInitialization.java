package com.armedia.acm.configuration.core.initialization;

/*-
 * #%L
 * ACM Tool Integrations: Configuration Library
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

import com.armedia.acm.configuration.service.FileConfigurationService;

import org.springframework.beans.factory.InitializingBean;

import java.util.List;

public class BrandingConfigurationInitialization implements InitializingBean
{

    private String customFilesLocation;

    private List<String> brandingFiles;

    private FileConfigurationService fileConfigurationService;

    @Override
    public void afterPropertiesSet() throws Exception
    {
        for (String file : brandingFiles)
        {
            fileConfigurationService.getFileFromConfiguration(file, customFilesLocation);
        }
    }

    public List<String> getBrandingFiles()
    {
        return brandingFiles;
    }

    public void setBrandingFiles(List<String> brandingFiles)
    {
        this.brandingFiles = brandingFiles;
    }

    public void setFileConfigurationService(FileConfigurationService fileConfigurationService)
    {
        this.fileConfigurationService = fileConfigurationService;
    }

    public String getCustomFilesLocation()
    {
        return customFilesLocation;
    }

    public void setCustomFilesLocation(String customFilesLocation)
    {
        this.customFilesLocation = customFilesLocation;
    }
}
