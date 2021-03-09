package com.armedia.acm.core;

/*-
 * #%L
 * ACM Core API
 * %%
 * Copyright (C) 2014 - 2021 ArkCase LLC
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.aop.support.AopUtils;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * <p>
 * The interface to be implemented by classes which hold configuration properties and also include properties which are changed in runtime.
 * </p>
 *
 * <p>
 * Instances of DynamicApplicationConfig are checked for any properties annotated with <code>{@link UnmodifiableConfigProperty}</code>.
 * Such properties are not allowed to be changed in runtime and are filtered to not be stored in runtime configuration file.
 * </p>
 */
public interface DynamicApplicationConfig
{
    Logger logger = LogManager.getLogger(DynamicApplicationConfig.class);

    @JsonIgnore
    default Map<String, Object> getManagedProperties()
    {
        Class beanClass = AopUtils.getTargetClass(this);
        Field[] fields = beanClass.getDeclaredFields();
        Map<String, Field> declaredFieldsMap = Arrays.stream(fields)
                .collect(Collectors.toMap(Field::getName, Function.identity()));
        Predicate<PropertyDescriptor> filterWithGetters = property -> property.getReadMethod() != null &&
                !property.getReadMethod().isAnnotationPresent(JsonIgnore.class);
        try
        {
            Map<String, Object> map = new HashMap<>();
            Arrays.stream(Introspector.getBeanInfo(beanClass, Object.class).getPropertyDescriptors())
                    .filter(filterWithGetters)
                    .filter(property -> {
                        Field field = declaredFieldsMap.get(property.getName());
                        // filter out unmodifiable properties
                        return field != null && !field.isAnnotationPresent(UnmodifiableConfigProperty.class);
                    })
                    .forEach(property -> {
                        String propKey = property.getName();
                        try
                        {
                            Field field = declaredFieldsMap.get(propKey);
                            if (field != null && field.isAnnotationPresent(JsonProperty.class))
                            {
                                JsonProperty jp = field.getAnnotation(JsonProperty.class);
                                propKey = jp.value();
                            }

                            Method method = property.getReadMethod();
                            Object propValue = method.invoke(this);
                            map.put(propKey, propValue);
                        }
                        catch (IllegalAccessException | InvocationTargetException e)
                        {
                            logger.warn("Failed to get value for property [{}]", propKey);
                        }
                    });
            return map;
        }
        catch (IntrospectionException e)
        {
            return Collections.emptyMap();
        }
    }
}
