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

import com.armedia.acm.plugins.admin.model.OCRConfiguration;
import com.armedia.acm.services.transcribe.exception.GetConfigurationException;
import com.armedia.acm.services.transcribe.exception.SaveConfigurationException;

public class ArkCaseOCRServiceImpl implements ArkCaseOCRService
{
    private OCRConfigurationPropertiesService ocrConfigurationPropertiesService;

    @Override
    public OCRConfiguration getConfiguration() throws GetConfigurationException
    {
        return getOCRConfigurationPropertiesService().get();
    }

    @Override
    public void saveConfiguration(OCRConfiguration configuration) throws SaveConfigurationException
    {
        if (configuration.isEnableOCR())
        {
            verifyOCR(configuration);
        }

        getOCRConfigurationPropertiesService().save(configuration);
    }

    @Override
    public void verifyOCR(OCRConfiguration configuration) throws SaveConfigurationException
    {
        Runtime rt = Runtime.getRuntime();
        Process pr;
        try
        {
            pr = rt.exec("tesseract --version");

            pr.waitFor();
        }
        catch (Exception e)
        {
            throw new SaveConfigurationException("The tesseract engine must be installed in order to enable OCR");
        }

        try
        {
            pr = rt.exec("qpdf --version");

            pr.waitFor();
        }
        catch (Exception e)
        {
            throw new SaveConfigurationException("The qpdf engine must be installed in order to enable OCR");
        }
    }

    public OCRConfigurationPropertiesService getOCRConfigurationPropertiesService()
    {
        return ocrConfigurationPropertiesService;
    }

    public void setOCRConfigurationPropertiesService(
            OCRConfigurationPropertiesService ocrConfigurationPropertiesService)
    {
        this.ocrConfigurationPropertiesService = ocrConfigurationPropertiesService;
    }
}
