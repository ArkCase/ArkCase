package com.armedia.acm.report.config;

/*-
 * #%L
 * Tool Integrations: report Configuration
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import com.armedia.acm.configuration.annotations.MapValue;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

public class ReportsParametersConfig
{
    @Value("${report.config.reportsParametersConfiguration.description}")
    private String description;

    public String getDescription()
    {
        return description;
    }


    public void setDescription(String description)
    {
        this.description = description;
    }

    private Map<String, String> dateSearchTypes = new HashMap<>();

    @MapValue(value = "report.config.reportsParametersConfiguration.dateSearchTypes")
    public Map<String, String> getDateSearchTypes()
    {
        return dateSearchTypes;
    }

    public void setDateSearchTypes(Map<String, String> dateSearchTypes)
    {
        this.dateSearchTypes = dateSearchTypes;
    }

    private Map<String, String> outputType = new HashMap<>();

    @MapValue(value = "report.config.reportsParametersConfiguration.outputType")
    public Map<String, String> getOutputType()
    {
        return outputType;
    }

    public void setOutputType(Map<String, String> outputType)
    {
        this.outputType = outputType;
    }
}
