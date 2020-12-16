package com.armedia.acm.services.holiday.service.web.api;

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

import com.armedia.acm.services.holiday.model.BusinessHoursConfig;
import com.armedia.acm.services.holiday.service.BusinessHoursConfigurationService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Aleksandar Acevski <aleksandar.acevski@armedia.com> on December, 2020
 */
@Controller
@RequestMapping({ "/api/v1/plugin/admin/businessHours", "/api/latest/plugin/admin/businessHours" })
public class BusinessHoursConfigurationAPIController
{

    private transient final Logger log = LogManager.getLogger(getClass());

    private BusinessHoursConfigurationService businessHoursConfigurationService;

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    public void updateBusinessHoursConfig(@RequestBody BusinessHoursConfig applicationConfig)
    {
        businessHoursConfigurationService.writeConfiguration(applicationConfig);
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public BusinessHoursConfig getBusinessHoursConfig()
    {
        return businessHoursConfigurationService.readConfiguration();
    }

    public void setBusinessHoursConfigurationService(BusinessHoursConfigurationService businessHoursConfigurationService)
    {
        this.businessHoursConfigurationService = businessHoursConfigurationService;
    }
}
