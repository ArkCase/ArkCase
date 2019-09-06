package com.armedia.acm;

/*-
 * #%L
 * ACM Standard Application: Law Enforcement
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

import com.armedia.acm.audit.service.AuditPatternsSubstitution;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.util.Properties;

/**
 * @author ivana.shekerova on 8/5/2019.
 */
public class AcmApplicationContextInitializer implements
        ApplicationContextInitializer<ConfigurableApplicationContext>
{

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext)
    {
        String confYamlPath = System.getProperty("acm.configurationserver.propertyfile");
        Resource yamlResource = new FileSystemResource(confYamlPath);

        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(yamlResource);
        Properties properties = yaml.getObject();

        System.setProperty("configuration.server.update.file.path", properties.getProperty("configuration.server.update.file.path"));
        System.setProperty("configuration.server.url", properties.getProperty("configuration.server.url"));
        System.setProperty("application.name", properties.getProperty("application.name"));
        System.setProperty("application.profile", properties.getProperty("application.profile"));
        System.setProperty("application.profile.reversed",
                AuditPatternsSubstitution.getProfilesReversed(properties.getProperty("application.profile")));
    }
}
