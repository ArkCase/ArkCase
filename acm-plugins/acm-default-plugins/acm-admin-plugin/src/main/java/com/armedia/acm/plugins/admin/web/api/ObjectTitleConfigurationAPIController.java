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

import com.armedia.acm.plugins.admin.model.ObjectTitleConfiguration;
import com.armedia.acm.plugins.admin.service.ObjectTitleConfigurationService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({ "/api/v1/service/object-title-config", "/api/latest/service/object-title-config" })
public class ObjectTitleConfigurationAPIController
{
    private ObjectTitleConfigurationService objectTitleConfigurationService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public void saveObjectTitleConfiguration(@RequestBody ObjectTitleConfiguration objectTitleConfiguration)
    {
        objectTitleConfigurationService.saveObjectTitleConfiguration(objectTitleConfiguration);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ObjectTitleConfiguration getObjectTitleConfiguration()
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
