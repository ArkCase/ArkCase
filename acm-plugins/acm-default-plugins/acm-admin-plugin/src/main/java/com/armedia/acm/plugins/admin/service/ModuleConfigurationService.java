package com.armedia.acm.plugins.admin.service;

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

import com.armedia.acm.plugins.admin.exception.AcmModuleConfigurationException;
import com.armedia.acm.plugins.admin.model.ModuleConfigurationConstants;
import com.armedia.acm.plugins.admin.model.ModuleItem;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Created by sergey.kolomiets on 6/26/15.
 */
public class ModuleConfigurationService implements ModuleConfigurationConstants
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private String appConfigPropertiesFile;

    public List<ModuleItem> retrieveModules() throws AcmModuleConfigurationException
    {
        try (InputStream propInputStream = FileUtils.openInputStream(new File(appConfigPropertiesFile)))
        {
            Properties props = new Properties();
            props.load(propInputStream);

            // Get only navigator keys
            List<String> modulesIds = new ArrayList<>();
            for (Object keyIter : props.keySet())
            {
                String key = (String) keyIter;
                if (key.indexOf(PROP_NAVIGATOR) == 0)
                {
                    // Remove prefix
                    int beginIndex = key.indexOf(".");
                    if (beginIndex == -1)
                    {
                        throw new AcmModuleConfigurationException(String.format("Wrong property name %s", (String) keyIter));
                    }
                    key = key.substring(beginIndex + 1);

                    // Remove suffix
                    int endIndex = key.indexOf(".");
                    if (beginIndex == -1)
                    {
                        throw new AcmModuleConfigurationException(String.format("Wrong property name %s", (String) keyIter));
                    }
                    key = key.substring(0, endIndex);
                    if (!modulesIds.contains(key))
                    {
                        modulesIds.add(key);
                    }

                    if (!modulesIds.contains(key))
                    {
                        modulesIds.add(key);
                    }
                }
            }

            List<ModuleItem> modulesInfos = new ArrayList<>();
            String nameProperty = "";
            String privilegeProperty = "";
            for (String moduleId : modulesIds)
            {
                nameProperty = String.format(PROP_MODULE_NAME_TMPL, moduleId);
                privilegeProperty = String.format(PROP_MODULE_PRIVILEGE_TMPL, moduleId);

                ModuleItem moduleItem = new ModuleItem();
                moduleItem.setId(moduleId);
                moduleItem.setName(props.getProperty(nameProperty, ""));
                moduleItem.setPrivilege(props.getProperty(privilegeProperty));
                moduleItem.setKey(props.getProperty(privilegeProperty));

                modulesInfos.add(moduleItem);
            }

            // Get only modules names
            return modulesInfos;

        }
        catch (Exception e)
        {
            log.error("Can't retrieve modules", e);
            throw new AcmModuleConfigurationException("Can't retrieve modules", e);
        }
    }

    public List<ModuleItem> findModulesPaged(Integer startRow, Integer maxRows, String sortDirection)
            throws AcmModuleConfigurationException
    {
        List<ModuleItem> modules = retrieveModules();
        modules.sort(Comparator.comparing(ModuleItem::getName));

        if (sortDirection.equalsIgnoreCase("DESC"))
        {
            modules.sort(Comparator.comparing(ModuleItem::getName).reversed());
        }
        else
        {
            modules.sort(Comparator.comparing(ModuleItem::getName));
        }

        return modules.stream().skip(startRow).limit(maxRows).collect(Collectors.toList());
    }

    public List<ModuleItem> findModulesByMatchingName(String filterQuery, Integer startRow, Integer maxRows, String sortDirection)
            throws AcmModuleConfigurationException
    {
        List<ModuleItem> modules = retrieveModules();

        modules = modules.stream().filter(moduleItem -> moduleItem.getName().toLowerCase().contains(filterQuery.toLowerCase()))
                .collect(Collectors.toList());

        if (sortDirection.contains("DESC"))
        {
            modules.sort(Comparator.comparing(ModuleItem::getName).reversed());
        }
        else
        {
            modules.sort(Comparator.comparing(ModuleItem::getName));
        }

        return modules.stream().skip(startRow).limit(maxRows).collect(Collectors.toList());
    }

    public void setAppConfigPropertiesFile(String appConfigPropertiesFile)
    {
        this.appConfigPropertiesFile = appConfigPropertiesFile;
    }
}
