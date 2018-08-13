package com.armedia.acm.pluginmanager.service;

import com.armedia.acm.pluginmanager.model.AcmPluginConfig;
import com.armedia.acm.spring.SpringContextHolder;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AcmConfigurablePluginsManager
{
    private SpringContextHolder contextHolder;

    public Map<String, AcmPluginConfig> getConfigurablePlugins()
    {
        Map<String, AcmConfigurablePlugin> configurablePlugins = contextHolder.getAllBeansOfType(AcmConfigurablePlugin.class);

        return configurablePlugins.values().stream()
                .map(it -> new AcmPluginConfig(it.getName(), it.isEnabled()))
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
