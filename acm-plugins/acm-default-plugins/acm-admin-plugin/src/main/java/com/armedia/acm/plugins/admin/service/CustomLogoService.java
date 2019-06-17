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

import com.armedia.acm.plugins.admin.exception.AcmCustomLogoException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.core.io.InputStreamSource;

import java.io.File;
import java.io.InputStream;

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

    /**
     * Return Header logo
     *
     * @return
     * @throws AcmCustomLogoException
     */
    public byte[] getHeaderLogo() throws AcmCustomLogoException
    {
        try
        {
            File headerLogo = new File(brandingFilesLocation + headerLogoFile);
            byte[] result = FileUtils.readFileToByteArray(headerLogo);
            return result;
        }
        catch (Exception e)
        {
            log.error("Can't get custom Header Logo file", e);
            throw new AcmCustomLogoException("Can't get custom Header Logo file", e);
        }
    }

    /**
     * Return Login logo
     *
     * @return
     * @throws AcmCustomLogoException
     */
    public byte[] getLoginLogo() throws AcmCustomLogoException
    {
        try
        {
            File loginLogo = new File(brandingFilesLocation + loginLogoFile);
            byte[] result = FileUtils.readFileToByteArray(loginLogo);
            return result;
        }
        catch (Exception e)
        {
            log.error("Can't get custom Login Logo file", e);
            throw new AcmCustomLogoException("Can't get custom Login Logo file", e);
        }
    }

    /**
     * Return email logo
     *
     * @return
     * @throws AcmCustomLogoException
     */
    public byte[] getEmailLogo() throws AcmCustomLogoException
    {
        try
        {
            File emailLogo = new File(brandingFilesLocation + emailLogoFile);
            byte[] result = FileUtils.readFileToByteArray(emailLogo);
            return result;
        }
        catch (Exception e)
        {
            log.error("Can't get custom Email Logo file", e);
            throw new AcmCustomLogoException("Can't get custom Email Logo file", e);
        }
    }

    public void updateLoginLogo(InputStreamSource logoFileSource) throws AcmCustomLogoException
    {
        updateFile(logoFileSource, loginLogoFile);
    }

    public void updateHeaderLogo(InputStreamSource logoFileSource) throws AcmCustomLogoException
    {
        updateFile(logoFileSource, headerLogoFile);
    }

    public void updateEmailLogo(InputStreamSource logoFileSource) throws AcmCustomLogoException
    {
        updateFile(logoFileSource, emailLogoFile);
    }

    private void updateFile(InputStreamSource logoFileSource, String fileName) throws AcmCustomLogoException
    {
        File logoFile = null;
        try (InputStream logoStream = logoFileSource.getInputStream())
        {
            logoFile = new File(brandingFilesLocation + fileName);
            FileUtils.copyInputStreamToFile(logoStream, logoFile);
    
        }
        catch (Exception e)
        {
            throw new AcmCustomLogoException("Can't update logo file");
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
}
