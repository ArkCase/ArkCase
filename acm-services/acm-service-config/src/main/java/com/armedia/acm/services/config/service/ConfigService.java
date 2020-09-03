package com.armedia.acm.services.config.service;

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

import com.armedia.acm.services.config.model.AcmConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by bojan.milenkoski on 12.9.2017
 */
public class ConfigService
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private List<AcmConfig> configList;

    public List<Map<String, String>> getInfo()
    {
        List<Map<String, String>> retval = new ArrayList<>();

        if (configList != null)
        {
            // With Java 8
            retval = configList.stream().map(this::createMap).filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        return retval;
    }

    private Map<String, String> createMap(AcmConfig acmConfig)
    {
        Map<String, String> retval = null;

        if (acmConfig != null)
        {
            retval = new HashMap<>();
            retval.put("name", acmConfig.getConfigName());
            retval.put("description", acmConfig.getConfigDescription());
        }

        return retval;
    }

    public String getConfigAsJson(String name)
    {
        String rc = "{}";
        try
        {
            Optional<AcmConfig> optionalRcString = configList.stream().filter(x -> x.getConfigName().equals(name)).findFirst();
            if (optionalRcString.isPresent())
                rc = optionalRcString.get().getConfigAsJson();
        }
        catch (NoSuchElementException e)
        {
            log.error(e.getMessage());
        }
        return rc;
    }

    public List<AcmConfig> getConfigList()
    {
        return configList;
    }

    public void setConfigList(List<AcmConfig> configList)
    {
        this.configList = configList;
    }
}
