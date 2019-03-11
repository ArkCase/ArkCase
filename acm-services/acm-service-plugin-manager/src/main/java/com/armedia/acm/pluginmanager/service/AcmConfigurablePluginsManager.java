package com.armedia.acm.pluginmanager.service;

/*-
 * #%L
 * ACM Service: Plugin Manager
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

import com.armedia.acm.pluginmanager.model.AcmPluginConfig;
import com.armedia.acm.spring.SpringContextHolder;
import org.springframework.aop.scope.ScopedProxyUtils;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AcmConfigurablePluginsManager
{
    private SpringContextHolder contextHolder;

    public Map<String, AcmPluginConfig> getConfigurablePlugins()
    {
        Map<String, AcmConfigurablePlugin> configurablePlugins = contextHolder.getAllBeansOfType(AcmConfigurablePlugin.class);

        return configurablePlugins.entrySet().stream()
                .filter(it -> !ScopedProxyUtils.isScopedTarget(it.getKey()))
                .map(it -> new AcmPluginConfig(it.getValue().getName(), it.getValue().isEnabled()))
                .collect(Collectors.toMap(AcmPluginConfig::getPluginName, Function.identity()));
    }

    public SpringContextHolder getContextHolder()
    {
        return contextHolder;
    }

    public void setContextHolder(SpringContextHolder contextHolder)
    {
        this.contextHolder = contextHolder;
    }
}
