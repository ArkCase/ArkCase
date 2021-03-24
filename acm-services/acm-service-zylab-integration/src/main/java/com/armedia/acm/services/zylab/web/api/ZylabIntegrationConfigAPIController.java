package com.armedia.acm.services.zylab.web.api;

/*-
 * #%L
 * ACM Service: Arkcase ZyLAB Integration
 * %%
 * Copyright (C) 2014 - 2021 ArkCase LLC
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.armedia.acm.services.zylab.service.ZylabIntegrationConfigService;
import com.armedia.acm.tool.zylab.model.ZylabIntegrationConfig;

/**
 * Created by Aleksandar Acevski <aleksandar.acevski@armedia.com> on February, 2021
 */
@Controller
@RequestMapping({ "/api/v1/plugin/admin", "/api/latest/plugin/admin" })
public class ZylabIntegrationConfigAPIController
{

    private Logger log = LogManager.getLogger(getClass());
    private ZylabIntegrationConfigService zylabIntegrationConfigService;

    @RequestMapping(value = "/zylab/configuration", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ZylabIntegrationConfig getConfiguration()
    {
        return getZylabIntegrationConfigService().readConfiguration();
    }

    @RequestMapping(value = "/zylab/configuration", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void updateConfiguration(@RequestBody ZylabIntegrationConfig zylabIntegrationConfig)
    {
        getZylabIntegrationConfigService().writeConfiguration(zylabIntegrationConfig);
    }

    public ZylabIntegrationConfigService getZylabIntegrationConfigService()
    {
        return zylabIntegrationConfigService;
    }

    public void setZylabIntegrationConfigService(ZylabIntegrationConfigService zylabIntegrationConfigService)
    {
        this.zylabIntegrationConfigService = zylabIntegrationConfigService;
    }
}
