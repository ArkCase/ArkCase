package gov.foia.web.api;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import gov.foia.model.FoiaConfiguration;
import gov.foia.service.FoiaConfigurationService;

@Controller
@RequestMapping({ "api/v1/service/foia/configuration", "api/latest/service/foia/configuration" })
public class FoiaConfigurationAPIController
{

    private FoiaConfigurationService foiaConfigurationService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public void updateFoiaConfigurationFile(@RequestBody FoiaConfiguration foiaConfiguration)
    {
        foiaConfigurationService.writeConfiguration(foiaConfiguration);
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public FoiaConfiguration getFoiaConfigurationFile()
    {
        return getFoiaConfigurationService().readConfiguration();
    }

    @RequestMapping(path = "/dashboardBannerConfiguration", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public boolean dashboardBannerConfiguration()
    {
        return getFoiaConfigurationService().readConfiguration().getDashboardBannerEnabled();
    }

    public FoiaConfigurationService getFoiaConfigurationService()
    {
        return foiaConfigurationService;
    }

    public void setFoiaConfigurationService(FoiaConfigurationService foiaConfigurationService)
    {
        this.foiaConfigurationService = foiaConfigurationService;
    }

}
