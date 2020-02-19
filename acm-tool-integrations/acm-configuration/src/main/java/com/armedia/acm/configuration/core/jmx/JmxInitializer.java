package com.armedia.acm.configuration.core.jmx;

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

import com.armedia.acm.configuration.core.ConfigurationContainer;
import com.armedia.acm.configuration.core.LabelsConfiguration;
import com.armedia.acm.configuration.core.LdapConfiguration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.export.annotation.AnnotationJmxAttributeSource;
import org.springframework.jmx.export.assembler.MetadataMBeanInfoAssembler;
import org.springframework.jmx.support.RegistrationPolicy;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class JmxInitializer
{
    @Autowired
    ConfigurationContainer configurationContainer;

    @Autowired
    LabelsConfiguration labelsConfiguration;

    @Autowired
    LdapConfiguration ldapConfiguration;

    @Bean(name = "coreRegisterer")
    public MBeanExporter getMBeanExporter()
    {
        MBeanExporter mBeanExporter = new MBeanExporter();
        Map<String, Object> beans = new HashMap<>();
        beans.put(
                "configuration:name=configuration-service,type=com.armedia.acm.configuration.ConfigurationService,artifactId=configuration-service",
                new ConfigurationFacadeJmx(configurationContainer));
        beans.put(
                "configuration:name=labels-service,type=com.armedia.acm.configuration.ConfigurationService,artifactId=labels-service",
                new ConfigurationFacadeJmx(labelsConfiguration));
        beans.put(
                "configuration:name=ldap-service,type=com.armedia.acm.configuration.ConfigurationService,artifactId=ldap-service",
                new ConfigurationFacadeJmx(ldapConfiguration));
        mBeanExporter.setBeans(beans);
        MetadataMBeanInfoAssembler metadataMBeanInfoAssembler = new MetadataMBeanInfoAssembler();
        metadataMBeanInfoAssembler.setAttributeSource(new AnnotationJmxAttributeSource());
        mBeanExporter.setAssembler(metadataMBeanInfoAssembler);
        mBeanExporter.setRegistrationPolicy(RegistrationPolicy.REPLACE_EXISTING);
        return mBeanExporter;
    }
}
