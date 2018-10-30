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

import com.armedia.acm.plugins.admin.service.CostsheetPropertiesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping({ "/api/v1/service/costsheet", "/api/latest/service/costsheet" })
public class CostsheetConfigurationAPIController
{

    private Logger log = LoggerFactory.getLogger(getClass());

    private CostsheetPropertiesService costsheetPropertiesService;


    @RequestMapping(value = "/properties", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, String>> loadCostsheetProperties() throws IOException {
        try
        {
            return new ResponseEntity<>(getCostsheetPropertiesService().loadProperties(), HttpStatus.OK);
        }
        catch (IOException e)
        {
            log.error("Could not load Costsheet Properties File", e);
            throw e;
        }
    }

    @RequestMapping(value = "/properties", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void saveCostsheetProperties(@RequestBody Map<String, String> costsheetProperties) throws IOException {
        try
        {
            getCostsheetPropertiesService().saveProperties(costsheetProperties);
        }
        catch (IOException e)
        {
            log.error("Could not save Costsheet Properties File", e);
            throw e;
        }
    }

    public CostsheetPropertiesService getCostsheetPropertiesService()
    {
        return costsheetPropertiesService;
    }

    public void setCostsheetPropertiesService(CostsheetPropertiesService costsheetPropertiesService)
    {
        this.costsheetPropertiesService = costsheetPropertiesService;
    }
}
