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

import com.armedia.acm.plugins.admin.exception.AcmCmisConfigurationException;
import com.armedia.acm.plugins.admin.model.CmisConfigurationConstants;
import com.armedia.acm.plugins.admin.model.CmisDTO;
import com.armedia.acm.plugins.admin.service.CmisConfigurationService;

import com.google.gson.GsonBuilder;
import org.json.JSONObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * Created by nick.ferguson on 3/22/2017.
 */
@Controller
@RequestMapping({ "/api/v1/plugin/admin", "/api/latest/plugin/admin" })
public class CmisConfigurationCreateConfig
{
    private Logger log = LogManager.getLogger(getClass());
    private CmisConfigurationService cmisConfigurationService;

    @RequestMapping(value = "/cmisconfiguration/config", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)

    @ResponseBody
    public CmisDTO createDirectory(
            @RequestBody String resource) throws IOException, AcmCmisConfigurationException
    {
        try
        {
            JSONObject newCmisObject = new JSONObject(resource);
            log.debug("Attempting to create new CMIS configuration from: " + newCmisObject.toString());

            String id = newCmisObject.getString(CmisConfigurationConstants.CMIS_ID);
            log.debug("Found CMIS ID: " + id);

            if (id == null)
            {
                log.error("CMIS ID is undefined, unable to create CMIS config");
                throw new AcmCmisConfigurationException("ID is undefined");
            }

            HashMap<String, Object> props = cmisConfigurationService.getProperties(newCmisObject);

            // Create CMIS Configuration
            cmisConfigurationService.createCmisConfig(id, props);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            return gson.fromJson(resource, CmisDTO.class);
        }
        catch (Exception e)
        {
            log.error("Can't create CMIS config", e);
            throw new AcmCmisConfigurationException("Create CMIS config error", e);
        }
    }

    public void setCmisConfigurationService(CmisConfigurationService cmisConfigurationService)
    {
        this.cmisConfigurationService = cmisConfigurationService;
    }
}
