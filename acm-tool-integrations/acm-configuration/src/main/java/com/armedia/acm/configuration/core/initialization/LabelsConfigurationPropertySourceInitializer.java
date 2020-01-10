package com.armedia.acm.configuration.core.initialization;

/*-
 * #%L
 * configuration-core
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

import com.armedia.acm.configuration.core.LabelsConfiguration;
import com.armedia.acm.configuration.core.propertysource.LabelsConfigServerPropertyResource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Configuration class for registering new custom property source which is the Spring Cloud Config Server.
 */

@Component("labelsConfig")
@DependsOn("bootstrapConfig")
public class LabelsConfigurationPropertySourceInitializer implements Ordered, InitializingBean
{

    @Autowired
    ConfigurableEnvironment configurableEnvironment;

    @Autowired
    LabelsConfiguration labelsConfiguration;

    private PropertySource getPropertySource()
    {
        labelsConfiguration.refresh();
        return new LabelsConfigServerPropertyResource(labelsConfiguration);
    }

    @Override
    public int getOrder()
    {
        return Ordered.HIGHEST_PRECEDENCE + 2;
    }

    @Override
    public void afterPropertiesSet()
    {
        MutablePropertySources propertySources = configurableEnvironment.getPropertySources();
        propertySources.addLast(getPropertySource());
    }
}
