package com.armedia.acm.plugins.admin.web.api;

/*-
 * #%L
 * ACM Default Plugin: admin
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import com.armedia.acm.plugins.admin.model.ObjectTitleConfig;
import com.armedia.acm.plugins.admin.model.TitleConfiguration;
import com.armedia.acm.plugins.admin.service.ObjectTitleConfigurationService;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping({ "/api/v1/service/object-title-config", "/api/latest/service/object-title-config" })
public class ObjectTitleConfigurationAPIController
{
    private ObjectTitleConfigurationService objectTitleConfigurationService;

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void saveObjectTitleConfiguration(@RequestBody  Map<String, TitleConfiguration> objectTitleConfiguration)
    {
        objectTitleConfigurationService.saveObjectTitleConfiguration(objectTitleConfiguration);
    }

    @RequestMapping(method = RequestMethod.GET,  produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, TitleConfiguration> getObjectTitleConfiguration()
    {
        return objectTitleConfigurationService.getObjectTitleConfig();
    }

    public ObjectTitleConfigurationService getObjectTitleConfigurationService()
    {
        return objectTitleConfigurationService;
    }

    public void setObjectTitleConfigurationService(ObjectTitleConfigurationService objectTitleConfigurationService)
    {
        this.objectTitleConfigurationService = objectTitleConfigurationService;
    }
}
