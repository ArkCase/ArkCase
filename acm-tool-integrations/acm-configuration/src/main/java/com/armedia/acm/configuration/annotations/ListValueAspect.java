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
