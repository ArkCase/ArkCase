package com.armedia.acm.configuration.yaml;

/*-
 * #%L
 * configuration-yaml
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

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Class to initialize configuration bean that holds properties provided in yaml configuration source.
 */
@Configuration
public class YamlInitializer
{
    @Bean
    public YamlFileConfiguration getYamlFileConfiguration()
    {
        YamlFileConfiguration yamlFileConfiguration;
        try
        {
            yamlFileConfiguration = new YamlFileConfiguration(System.getProperty("acm.configurationserver.propertyfile"));
        }
        catch (ConfigurationException e)
        {
            throw new com.armedia.acm.configuration.yaml.ConfigurationException("Configuration failed to initialize.", e);
        }
        FileChangedReloadingStrategy fileChangedReloadingStrategy = new FileChangedReloadingStrategy();
        fileChangedReloadingStrategy.setRefreshDelay(10000);
        yamlFileConfiguration.setReloadingStrategy(fileChangedReloadingStrategy);
        return yamlFileConfiguration;
    }
}
