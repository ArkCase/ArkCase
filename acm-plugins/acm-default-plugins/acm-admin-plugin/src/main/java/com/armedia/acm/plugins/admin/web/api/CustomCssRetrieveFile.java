package com.armedia.acm.plugins.admin.web.api;

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
import com.armedia.acm.plugins.admin.service.CustomCssService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

/**
 * Created by sergey.kolomiets on 6/19/15.
 */
@Controller
@RequestMapping({ "/branding" })
public class CustomCssRetrieveFile
{
    private Logger log = LogManager.getLogger(getClass());
    private CustomCssService customCssService;

    @RequestMapping(value = "/customcss", method = RequestMethod.GET, produces = "text/css")
    @ResponseBody
    public String retrieveFile() throws IOException, AcmCustomCssException
    {

        try
        {
            String customCSSFileContent = customCssService.getFile();
            return customCSSFileContent;
        }
        catch (Exception e)
        {
            log.error("Can't get custom CSS file", e);
            throw new AcmCustomCssException("Can't get custom CSS file", e);
        }
    }

    public void setCustomCssService(CustomCssService customCssService)
    {
        this.customCssService = customCssService;
    }
}
