package com.armedia.acm.configuration.core.initialization;

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
