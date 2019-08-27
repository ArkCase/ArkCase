package com.armedia.acm.configuration.annotations;

import com.armedia.acm.configuration.service.ConfigurationPropertyService;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Around aspect targeting annotation: {@link ListValue}
 *
 * Created by mario.gjurcheski on 08/16/2019.
 */
@Aspect
@Configuration
public class ListValueAspect
{

    @Autowired
    private ConfigurationPropertyService configurationPropertyService;

    /**
     * Around aspect targeting annotation: @{@link ListValue}
     * Handled Responses:
     */
    @Around(value = "@annotation(property)")
    public Object aroundListPropertyValueDecoratingMethod(ProceedingJoinPoint pjp, ListValue property)
    {
        List<Object> listPropertyValue = (List<Object>) configurationPropertyService.getProperty(property.value());

        return listPropertyValue;
    }

}
