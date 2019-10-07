package com.armedia.acm.configuration.refresher.jmx;

import com.armedia.acm.configuration.api.ConfigurationFacade;
import com.armedia.acm.configuration.api.RefreshScopeFacade;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jmx.access.MBeanProxyFactoryBean;

import javax.management.MalformedObjectNameException;

/*-
 * #%L
 * configuration-refresher
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

@Configuration
public class JmxProxyInitializer
{

    @Bean(name = "proxyRefreshScopeFacade")
    public MBeanProxyFactoryBean getProxyRefreshScopeFacade() throws MalformedObjectNameException
    {
        MBeanProxyFactoryBean mBeanProxyFactoryBean = new MBeanProxyFactoryBean();
        mBeanProxyFactoryBean.setObjectName(
                "configuration:name=configuration-service,type=com.armedia.acm.configuration.RefreshScope,artifactId=configuration-service");
        mBeanProxyFactoryBean.setProxyInterface(RefreshScopeFacade.class);

        return mBeanProxyFactoryBean;
    }

    @Bean(name = "proxyConfigurationFacade")
    public MBeanProxyFactoryBean getMBeanProxyFactoryBean() throws MalformedObjectNameException
    {
        MBeanProxyFactoryBean mBeanProxyFactoryBean = new MBeanProxyFactoryBean();
        mBeanProxyFactoryBean.setObjectName(
                "configuration:name=configuration-service,type=com.armedia.acm.configuration.ConfigurationService,artifactId=configuration-service");
        mBeanProxyFactoryBean.setProxyInterface(ConfigurationFacade.class);

        return mBeanProxyFactoryBean;
    }

    @Bean(name = "proxyLabelsFacade")
    public MBeanProxyFactoryBean getMBeanProxyLabelsFactoryBean() throws MalformedObjectNameException
    {
        MBeanProxyFactoryBean mBeanProxyFactoryBean = new MBeanProxyFactoryBean();
        mBeanProxyFactoryBean.setObjectName(
                "configuration:name=labels-service,type=com.armedia.acm.configuration.ConfigurationService,artifactId=labels-service");
        mBeanProxyFactoryBean.setProxyInterface(ConfigurationFacade.class);

        return mBeanProxyFactoryBean;
    }
}
