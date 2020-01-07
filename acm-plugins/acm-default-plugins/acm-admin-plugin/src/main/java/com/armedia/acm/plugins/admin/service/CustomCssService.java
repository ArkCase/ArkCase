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

import com.armedia.acm.plugins.admin.exception.AcmCustomCssException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.File;

/**
 * Created by admin on 6/11/15.
 */
public class CustomCssService
{
    private Logger log = LogManager.getLogger(getClass());

    private String customCssFile;

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
            File cssFile = new File(customCssFile);
            FileUtils.writeStringToFile(cssFile, cssText);

        }
        catch (Exception e)
        {
            log.error("Can't update custom CSS file [{}]", customCssFile, e);
            throw new AcmCustomCssException(String.format("Can't update custom CSS file %s", customCssFile), e);
        }
    }

    public void setCustomCssFile(String customCssFile)
    {
        this.customCssFile = customCssFile;
    }
}
