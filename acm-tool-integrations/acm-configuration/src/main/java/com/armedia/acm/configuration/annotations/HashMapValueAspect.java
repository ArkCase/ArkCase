package com.armedia.acm.configuration.annotations;

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

import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.AbstractMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Around aspect targeting annotation: {@link HashMapValue}
 *
 * Created by mario.gjurcheski on 08/16/2019.
 */
@Aspect
@Configuration
public class HashMapValueAspect
{
    @Autowired
    private ConfigurationContainer configurationContainer;

    /**
     * Around aspect targeting annotation: @{@link HashMapValue}
     * Handled Responses:
     */
    @Around(value = "@annotation(propertyKey)")
    public Object aroundMapPropertyValueDecoratingMethod(HashMapValue propertyKey)
    {

        Map<String, Object> propsFromConfiguration = configurationContainer.getConfigurationMap();

        Function<Map.Entry<String, Object>, Map.Entry<String, Object>> transform = entry -> {

            String newKey = entry.getKey().replace(propertyKey.value() + ".", "");
            return new AbstractMap.SimpleEntry<>(newKey, entry.getValue());

        };

        // filter all the properties from configuration that contains the propertyKey from the annotation
        Map<String, Object> props = propsFromConfiguration.entrySet()
                .stream()
                .filter(s -> s.getKey().startsWith(propertyKey.value() + "."))
                .map(transform)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        props.remove(propertyKey.value());

        return configurationContainer.convertMap(props, false);
    }

}

