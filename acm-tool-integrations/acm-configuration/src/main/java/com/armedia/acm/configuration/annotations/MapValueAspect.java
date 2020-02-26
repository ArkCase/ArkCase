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
import com.armedia.acm.configuration.core.LdapConfiguration;
import com.armedia.acm.configuration.service.CollectionPropertiesConfigurationService;

import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Around aspect targeting annotation: {@link MapValue}
 *
 * Created by mario.gjurcheski on 08/16/2019.
 */
@Aspect
@Component
public class MapValueAspect
{
    @Autowired
    private ConfigurationContainer configurationContainer;

    @Autowired
    private LdapConfiguration ldapConfiguration;

    @Autowired
    private CollectionPropertiesConfigurationService collectionPropertiesConfigurationService;

    /**
     * Around aspect targeting annotation: @{@link MapValue}
     * Handled Responses:
     */
    @Around(value = "@annotation(propertyKey)")
    public Object aroundMapPropertyValueDecoratingMethod(MapValue propertyKey)
    {
        Map<String, Object> propsFromConfiguration;

        if (propertyKey.configurationName().equals("ldapConfiguration"))
        {
            propsFromConfiguration = ldapConfiguration.getLdapDefaultMap();
        }
        else
        {
            propsFromConfiguration = configurationContainer.getConfigurationMap();
        }

        return collectionPropertiesConfigurationService.filterAndConvertProperties(propertyKey.value() + ".",
                propsFromConfiguration, false, propertyKey.convertFromTheRootKey());
    }

}
