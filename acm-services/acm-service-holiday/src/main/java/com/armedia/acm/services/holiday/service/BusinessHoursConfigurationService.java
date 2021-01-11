package com.armedia.acm.services.holiday.service;

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

import com.armedia.acm.configuration.service.ConfigurationPropertyService;
import com.armedia.acm.services.holiday.model.BusinessHoursConfig;

/**
 * Created by Aleksandar Acevski <aleksandar.acevski@armedia.com> on December, 2020
 */
public class BusinessHoursConfigurationService
{

    private BusinessHoursConfig businessHoursConfig;
    private ConfigurationPropertyService configurationPropertyService;

    public void writeConfiguration(BusinessHoursConfig applicationConfig)
    {
        configurationPropertyService.updateProperties(applicationConfig);
    }

    public BusinessHoursConfig readConfiguration()
    {
        return businessHoursConfig;
    }

    public void setBusinessHoursConfig(BusinessHoursConfig businessHoursConfig)
    {
        this.businessHoursConfig = businessHoursConfig;
    }

    public void setConfigurationPropertyService(ConfigurationPropertyService configurationPropertyService)
    {
        this.configurationPropertyService = configurationPropertyService;
    }
}
