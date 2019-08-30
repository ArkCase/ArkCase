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

import com.armedia.acm.configuration.service.FileConfigurationService;
import com.armedia.acm.plugins.admin.exception.AcmCustomCssException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.InputStreamResource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Created by admin on 6/11/15.
 */
public class CustomCssService
{
    private Logger log = LogManager.getLogger(getClass());

    private String customCssFile;

    private String customCssFileName;

    private FileConfigurationService fileConfigurationService;

    public String getFile()
    {
        String fileContent = "";
        try
        {
            File cssFile = new File(customCssFile);
            if (cssFile.exists())
            {
                fileContent = FileUtils.readFileToString(cssFile);
            }
        }
        catch (Exception e)
        {
            log.error("Can't get custom CSS file [{}]", customCssFile, e);
        }
        return fileContent;
    }

    public void updateFile(String cssText) throws AcmCustomCssException
    {
        try
        {
            InputStreamResource file = setInputStreamResource(cssText);

            fileConfigurationService.moveFileToConfiguration(file, "branding/" + customCssFileName);

        }
        catch (Exception e)
        {
            log.error("Can't update custom CSS file [{}]", customCssFile, e);
            throw new AcmCustomCssException(String.format("Can't update custom CSS file %s", customCssFile), e);
        }
    }

    private InputStreamResource setInputStreamResource(String cssText)
    {
        InputStream stream = new ByteArrayInputStream(cssText.getBytes(StandardCharsets.UTF_8));
        InputStreamResource file = new InputStreamResource(stream);
        return file;
    }

    public void setCustomCssFile(String customCssFile)
    {
        this.customCssFile = customCssFile;
    }

    public void setFileConfigurationService(FileConfigurationService fileConfigurationService)
    {
        this.fileConfigurationService = fileConfigurationService;
    }

    public void setCustomCssFileName(String customCssFileName)
    {
        this.customCssFileName = customCssFileName;
    }
}
