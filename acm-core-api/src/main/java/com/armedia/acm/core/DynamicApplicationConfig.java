package com.armedia.acm.core;

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
