package com.armedia.acm.configuration.service;

/*-
 * #%L
 * ACM Tool Integrations: Configuration Library
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

import com.armedia.acm.configuration.core.ConfigurationContainer;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service("configurationPropertyService")
public class ConfigurationPropertyService implements InitializingBean
{
    @Autowired
    private ConfigurationContainer configurationContainer;

    @Autowired
    private RestTemplate configRestTemplate;

    @Autowired
    private ConfigurableEnvironment configurableEnvironment;

    private String updatePropertiesEndpoint;

    private static final Logger log = LogManager.getLogger(ConfigurationPropertyService.class);

    @Override
    public void afterPropertiesSet()
    {
        String updatePath = (String) configurableEnvironment.getPropertySources()
                .get("bootstrap").getProperty("configuration.server.update.path");
        String serverUrl = (String) configurableEnvironment.getPropertySources()
                .get("bootstrap").getProperty("configuration.server.url");
        updatePropertiesEndpoint = String.format("%s%s", serverUrl, updatePath);
    }

    @Autowired
    private @Qualifier("sourceObjectMapper") ObjectMapper objectMapper;

    public Object getProperty(String propertyKey)
    {
        return configurationContainer.getProperty(propertyKey);
    }

    public Map<String, Object> getProperties(Object propertyConfig)
    {
        if (propertyConfig == null)
        {
            return new HashMap<>();
        }

        Class targetClass = AopUtils.getTargetClass(propertyConfig);
        try
        {
            String json = objectMapper.writerFor(targetClass)
                    .writeValueAsString(propertyConfig);
            return objectMapper.readerFor(HashMap.class).readValue(json);
        }
        catch (IOException e)
        {
            log.warn("Can't convert configuration object [{}] to map of properties", targetClass);
            return new HashMap<>();
        }
    }

    public void updateProperties(Map<String, Object> properties) throws ConfigurationPropertyException
    {
        try
        {
            configRestTemplate.postForEntity(updatePropertiesEndpoint, properties, ResponseEntity.class, Collections.EMPTY_MAP);
        }
        catch (RestClientException e)
        {
            log.warn("Failed to update property due to {}", e.getMessage());
            throw new ConfigurationPropertyException("Failed to update configuration");
        }
    }

    public void updateProperties(Object propertyConfig) throws ConfigurationPropertyException
    {
        try
        {
            configRestTemplate.postForEntity(updatePropertiesEndpoint,
                    getProperties(propertyConfig), ResponseEntity.class, Collections.EMPTY_MAP);
        }
        catch (RestClientException e)
        {
            log.warn("Failed to update property due to {}", e.getMessage());
            throw new ConfigurationPropertyException("Failed to update configuration", e);
        }
    }
}
