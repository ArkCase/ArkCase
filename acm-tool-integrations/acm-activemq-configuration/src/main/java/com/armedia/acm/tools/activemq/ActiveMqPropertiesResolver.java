package com.armedia.acm.tools.activemq;

/*-
 * #%L
 * Tool Integrations: ActiveMQ Configuration
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

import com.armedia.acm.configuration.client.ConfigurationServiceBootClient;
import com.armedia.acm.configuration.yaml.YamlFileConfiguration;
import com.armedia.acm.configuration.yaml.YamlInitializer;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * @author ivana.shekerova on 8/14/2019.
 */
public class ActiveMqPropertiesResolver
{

    private YamlFileConfiguration yamlFileConfiguration;
    private YamlInitializer yamlInitializer;
    private ConfigurableEnvironment configurableEnvironment;
    private ConfigurationServiceBootClient configurationServiceBootClient;

    public Properties getProperties()
    {
        configurationServiceBootClient = new ConfigurationServiceBootClient();
        yamlInitializer = new YamlInitializer();
        yamlFileConfiguration = yamlInitializer.getYamlFileConfiguration();
        configurableEnvironment = new StandardEnvironment();

        MutablePropertySources propertySources = configurableEnvironment.getPropertySources();
        propertySources.addFirst(new MapPropertySource("bootstrap", prepareConfigurationMap()));
        configurationServiceBootClient.setConfigurableEnvironment(configurableEnvironment);
        configurationServiceBootClient.setEnvironment(new StandardEnvironment());

        Map<String, Object> configurationMap = configurationServiceBootClient.loadConfiguration("arkcase-activemq", null);
        Properties props = new Properties();
        props.putAll(configurationMap);

        return props;
    }

    private Map<String, Object> prepareConfigurationMap()
    {
        Iterator<String> it = yamlFileConfiguration.getKeys();
        Map<String, Object> result = new HashMap<>();
        it.forEachRemaining(key -> result.put(key, yamlFileConfiguration.getProperty(key)));
        return result;
    }
}
