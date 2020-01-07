package com.armedia.acm.services.config.web.api;

/*-
 * #%L
 * ACM Service: Config
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

import com.armedia.acm.services.config.service.ConfigService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping({ "/api/v1/service/config", "/api/latest/service/config" })
public class ConfigApiController
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private ConfigService configService;

    /**
     * This method returns json structure when /api/v1/service/config is called.
     *
     * @return - Returns json structure, list of config names and their descriptions
     */
    @RequestMapping(method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
    @ResponseBody
    public List<Map<String, String>> getInfo()
    {
        return configService.getInfo();
    }

    @RequestMapping(value = "/{name}", method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_JSON_UTF8_VALUE,
            MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public Object getConfig(@PathVariable("name") String name)
    {
        log.debug("Get [{}] configuration", name);
        return configService.getConfigAsJson(name);
    }

    public ConfigService getConfigService()
    {
        return configService;
    }

    public void setConfigService(ConfigService configService)
    {
        this.configService = configService;
    }
}
