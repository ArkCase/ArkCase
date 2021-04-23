package com.armedia.acm.plugins.admin.service;

/*-
 * #%L
 * ACM Default Plugin: admin
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

import com.armedia.acm.configuration.model.ConfigurationClientConfig;
import com.armedia.acm.configuration.service.FileConfigurationService;
import com.armedia.acm.plugins.admin.exception.AcmCustomLogoException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.InputStreamResource;

import java.io.File;
import java.io.IOException;

/**
 * Created by sergey.kolomiets on 6/22/15.
 */
public class CustomLogoService
{
    private Logger log = LogManager.getLogger(getClass());

    private String brandingFilesLocation;
    private String headerLogoFile;
    private String loginLogoFile;
    private String emailLogoFile;
    private String headerLogoPortalFile;
    private String loginLogoPortalFile;
    private String bannerLogoPortalFile;

    private FileConfigurationService fileConfigurationService;
    private ConfigurationClientConfig configurationClientConfig;

    /**
     * Return Header logo
     *
     * @return header logo
     * @throws AcmCustomLogoException
     */
    public byte[] getHeaderLogo() throws AcmCustomLogoException
    {
        return getLogoFile(headerLogoFile);
    }

    /**
     * Return Login logo
     *
     * @return login logo
     * @throws AcmCustomLogoException
     */
    public byte[] getLoginLogo() throws AcmCustomLogoException
    {
        return getLogoFile(loginLogoFile);
    }

    /**
     * Return email logo
     *
     * @return email logo
     * @throws AcmCustomLogoException
     */
    public byte[] getEmailLogo() throws AcmCustomLogoException
    {
        return getLogoFile(emailLogoFile);
    }

    /**
     * Return portal header logo
     *
     * @return portal header logo
     * @throws AcmCustomLogoException
     */
    public byte[] getHeaderLogoPortalFile() throws AcmCustomLogoException
    {
        return getLogoFile(headerLogoPortalFile);
    }

    /**
     * Return portal login logo
     *
     * @return portal login logo
     * @throws AcmCustomLogoException
     */
    public byte[] getLoginLogoPortalFile() throws AcmCustomLogoException
    {
        return getLogoFile(loginLogoPortalFile);
    }

    /**
     * Return portal banner logo
     *
     * @return portal banner logo
     * @throws AcmCustomLogoException
     */
    public byte[] getBannerLogoPortalFile() throws AcmCustomLogoException
    {
        return getLogoFile(bannerLogoPortalFile);
    }

    public void updateLoginLogo(InputStreamResource logoFileSource) throws IOException
    {
        updateFile(logoFileSource, loginLogoFile);
    }

    public void updateHeaderLogo(InputStreamResource logoFileSource) throws IOException
    {
        updateFile(logoFileSource, headerLogoFile);
    }

    public void updateEmailLogo(InputStreamResource logoFileSource) throws IOException
    {
        updateFile(logoFileSource, emailLogoFile);
    }

    public void updateLoginLogoPortal(InputStreamResource logoFileSource) throws IOException
    {
        updateFile(logoFileSource, loginLogoPortalFile);
    }

    public void updateHeaderLogoPortal(InputStreamResource logoFileSource) throws IOException
    {
        updateFile(logoFileSource, headerLogoPortalFile);
    }

    public void updateBannerLogoPortal(InputStreamResource logoFileSource) throws IOException
    {
        updateFile(logoFileSource, bannerLogoPortalFile);
    }

    private void updateFile(InputStreamResource logoFileSource, String fileName) throws IOException
    {
        log.debug("Trying to update the file with file name {} in the configuration.", fileName);
        fileConfigurationService.moveFileToConfiguration(logoFileSource,
                configurationClientConfig.getBrandingPath() + "/" + fileName);
    }

    private byte[] getLogoFile(String logoFile) throws AcmCustomLogoException
    {
        try
        {
            File logo = new File(brandingFilesLocation + logoFile);
            return FileUtils.readFileToByteArray(logo);
        }
        catch (Exception e)
        {
            log.error("Can't get custom Logo file {} {}", logoFile, e.getMessage());
            throw new AcmCustomLogoException("Can't get custom Logo file", e);
        }
    }

    public void setBrandingFilesLocation(String brandingFilesLocation)
    {
        this.brandingFilesLocation = brandingFilesLocation;
    }

    public void setHeaderLogoFile(String headerLogoFile)
    {
        this.headerLogoFile = headerLogoFile;
    }

    public void setLoginLogoFile(String loginLogoFile)
    {
        this.loginLogoFile = loginLogoFile;
    }

    public String getEmailLogoFile()
    {
        return emailLogoFile;
    }

    public void setEmailLogoFile(String emailLogoFile)
    {
        this.emailLogoFile = emailLogoFile;
    }

    public void setFileConfigurationService(FileConfigurationService fileConfigurationService)
    {
        this.fileConfigurationService = fileConfigurationService;
    }

    public void setConfigurationClientConfig(ConfigurationClientConfig configurationClientConfig)
    {
        this.configurationClientConfig = configurationClientConfig;
    }

    public void setHeaderLogoPortalFile(String headerLogoPortalFile)
    {
        this.headerLogoPortalFile = headerLogoPortalFile;
    }

    public void setLoginLogoPortalFile(String loginLogoPortalFile)
    {
        this.loginLogoPortalFile = loginLogoPortalFile;
    }

    public void setBannerLogoPortalFile(String bannerLogoPortalFile)
    {
        this.bannerLogoPortalFile = bannerLogoPortalFile;
    }
}
