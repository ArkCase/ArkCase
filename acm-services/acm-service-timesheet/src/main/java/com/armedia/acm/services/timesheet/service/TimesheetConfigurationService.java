package com.armedia.acm.services.timesheet.service;

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
import com.armedia.acm.configuration.util.MergeFlags;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.services.config.lookups.model.StandardLookupEntry;
import com.armedia.acm.services.config.lookups.service.LookupDao;
import com.armedia.acm.services.timesheet.model.TimesheetChargeRoleConfigItem;
import com.armedia.acm.services.timesheet.model.TimesheetChargeRolesConfig;
import com.armedia.acm.services.timesheet.model.TimesheetConfig;
import com.armedia.acm.services.timesheet.model.TimesheetConfigDTO;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class TimesheetConfigurationService
{
    private Logger log = LogManager.getLogger(getClass());

    private ObjectConverter objectConverter;

    private TimesheetConfig timesheetConfig;
    private TimesheetChargeRolesConfig timesheetChargeRolesConfig;
    private ConfigurationPropertyService configurationPropertyService;
    private LookupDao lookupDao;

    public TimesheetConfigDTO loadTimesheetChargeRolesConfig()
    {
        TimesheetConfigDTO dto = new TimesheetConfigDTO();
        dto.setChargeRoleItems(getChargeRoles());
        return dto;
    }

    public void saveTimesheetChargeRolesConfig(TimesheetConfigDTO timesheetConfigDTO)
    {
        TimesheetChargeRolesConfig config = new TimesheetChargeRolesConfig();
        config.setTimesheetConfigurationMap(toChargeRoles(timesheetConfigDTO.getChargeRoleItems()));
        configurationPropertyService.updateProperties(config);
    }

    public void saveProperties(TimesheetConfig timesheetConfig)
    {
        configurationPropertyService.updateProperties(timesheetConfig);
    }

    public List<TimesheetChargeRoleConfigItem> getChargeRoles()
    {
        List<TimesheetChargeRoleConfigItem> chargeRoles = new ArrayList<>();
        Map<String, Map<String, Object>> configMap = timesheetChargeRolesConfig.getTimesheetConfigurationMap();
        Map<String, Object> chargeRoleItemsMap = configMap.get("chargeRoleItems");
        if (Objects.nonNull(chargeRoleItemsMap))
        {
            for (Map.Entry<String, Object> chargeRoleItem : chargeRoleItemsMap.entrySet())
            {
                String chargeRoleItemName = chargeRoleItem.getKey();
                Map<String, Object> chargeRoleItemProps = (Map<String, Object>) chargeRoleItem.getValue();
                TimesheetChargeRoleConfigItem timesheetChargeRoleConfigItem = new TimesheetChargeRoleConfigItem();
                timesheetChargeRoleConfigItem.setChargeRole(chargeRoleItemName);
                timesheetChargeRoleConfigItem.setActive((String) chargeRoleItemProps.get("active"));
                timesheetChargeRoleConfigItem.setRate(BigDecimal.valueOf((Integer) chargeRoleItemProps.get("rate")));
                chargeRoles.add(timesheetChargeRoleConfigItem);
            }
        }
        return chargeRoles;
    }

    public Map<String, Map<String, Object>> toChargeRoles(List<TimesheetChargeRoleConfigItem> chargeRoleItems)
    {

        Map<String, Map<String, Object>> configMap = new LinkedHashMap<>();
        Map<String, Object> chargeRolesMap = new LinkedHashMap<>();
        configMap.put("chargeRoleItems", chargeRolesMap);
        Set<String> timesheetChargeRoles = ((List<StandardLookupEntry>) getLookupDao()
                .getLookupByName("timesheetChargeRoles").getEntries())
                .stream()
                .map(StandardLookupEntry::getKey).collect(Collectors.toSet());
        for (TimesheetChargeRoleConfigItem chargeRoleItem : chargeRoleItems)
        {
            String chargeRoleName = chargeRoleItem.getChargeRole();
            timesheetChargeRoles.remove(chargeRoleName);
            Map<String, Object> chargeRoleProps = new LinkedHashMap<>();
            putInMapIfValueNotNull(chargeRoleProps, "chargeRole", chargeRoleItem.getChargeRole());
            putInMapIfValueNotNull(chargeRoleProps, "active", chargeRoleItem.getActive());
            putInMapIfValueNotNull(chargeRoleProps, "rate", chargeRoleItem.getRate());
            chargeRolesMap.put(chargeRoleName, chargeRoleProps);
        }

        for (String removedChargeRole : timesheetChargeRoles)
        {
            chargeRolesMap.put(MergeFlags.REMOVE.getSymbol() + removedChargeRole, null);
        }

        return configMap;
    }

    private static <V> void putInMapIfValueNotNull(Map<String, Object> map, String key, V value)
    {
        if (Objects.nonNull(value))
        {
            map.put(key, value);
        }
    }

    public TimesheetConfig loadProperties()
    {
        return timesheetConfig;
    }

    public ObjectConverter getObjectConverter()
    {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
    }

    public TimesheetConfig getTimesheetConfig()
    {
        return timesheetConfig;
    }

    public void setTimesheetConfig(TimesheetConfig timesheetConfig)
    {
        this.timesheetConfig = timesheetConfig;
    }

    public ConfigurationPropertyService getConfigurationPropertyService()
    {
        return configurationPropertyService;
    }

    public void setConfigurationPropertyService(ConfigurationPropertyService configurationPropertyService)
    {
        this.configurationPropertyService = configurationPropertyService;
    }

    public TimesheetChargeRolesConfig getTimesheetChargeRolesConfig()
    {
        return timesheetChargeRolesConfig;
    }

    public void setTimesheetChargeRolesConfig(TimesheetChargeRolesConfig timesheetChargeRolesConfig)
    {
        this.timesheetChargeRolesConfig = timesheetChargeRolesConfig;
    }

    public LookupDao getLookupDao()
    {
        return lookupDao;
    }

    public void setLookupDao(LookupDao lookupDao)
    {
        this.lookupDao = lookupDao;
    }
}
