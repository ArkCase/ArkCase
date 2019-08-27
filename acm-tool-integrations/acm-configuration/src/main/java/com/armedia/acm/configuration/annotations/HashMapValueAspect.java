package com.armedia.acm.configuration.annotations;

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

