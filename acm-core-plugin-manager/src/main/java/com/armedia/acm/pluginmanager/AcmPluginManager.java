package com.armedia.acm.pluginmanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class AcmPluginManager implements ApplicationContextAware
{

    private Logger log = LoggerFactory.getLogger(getClass());

    private Collection<AcmPlugin> acmPlugins;
    private Collection<AcmPlugin> enabledNavigatorPlugins;

    public synchronized Collection<AcmPlugin> getAcmPlugins()
    {
        return Collections.unmodifiableCollection(acmPlugins);
    }

    public synchronized void registerPlugin(AcmPlugin plugin)
    {
        acmPlugins.add(plugin);

        if ( plugin.isNavigatorTab() && plugin.isEnabled() )
        {
            if ( log.isDebugEnabled() )
            {
                log.debug("Adding navigator plugin " + plugin.getPluginName());
            }
            enabledNavigatorPlugins.add(plugin);
        }
    }

    /**
     * Scan for bundled plugins at application start time.
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        acmPlugins = new ArrayList<>();
        enabledNavigatorPlugins = new ArrayList<>();

        Map<String, AcmPlugin> plugins = applicationContext.getBeansOfType(AcmPlugin.class);

        if ( log.isInfoEnabled() )
        {
            log.info(plugins.size() + " plugin(s) found.");
        }

        for ( Map.Entry<String, AcmPlugin> plugin : plugins.entrySet() )
        {
            if ( log.isDebugEnabled() )
            {
                log.debug("Registering plugin '" + plugin.getKey() + "' of type '" +
                        plugin.getValue().getClass().getName() + "'.");
            }
            registerPlugin(plugin.getValue());
        }
    }


    public synchronized Collection<AcmPlugin> getEnabledNavigatorPlugins()
    {
        return Collections.unmodifiableCollection(enabledNavigatorPlugins);
    }


}
