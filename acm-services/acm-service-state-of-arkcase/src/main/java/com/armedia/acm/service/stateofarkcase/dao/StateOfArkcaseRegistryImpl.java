package com.armedia.acm.service.stateofarkcase.dao;

import com.armedia.acm.service.stateofarkcase.interfaces.StateOfModuleProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StateOfArkcaseRegistryImpl implements StateOfArkcaseRegistry, ApplicationListener<ContextRefreshedEvent>
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private Map<String, StateOfModuleProvider> moduleStateProviders = new HashMap();

    @Override
    public void registerStateOfModuleProvider(StateOfModuleProvider stateOfModuleProvider)
    {
        moduleStateProviders.put(stateOfModuleProvider.getModuleName(), stateOfModuleProvider);
    }

    @Override
    public List<StateOfModuleProvider> getStateOfModuleProviders()
    {
        return new ArrayList<>(moduleStateProviders.values());
    }

    /**
     * Handle an application event.
     *
     * @param event
     *            the event to respond to
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event)
    {
        // after application is started, i.e. context is loaded, all the registered modules which provide state will be
        // collected
        ApplicationContext applicationContext = event.getApplicationContext();
        Map<String, StateOfModuleProvider> registeredbeans = applicationContext.getBeansOfType(StateOfModuleProvider.class);
        log.info("Mapping [{}] registered module state providers:", registeredbeans.entrySet().size());
        for (StateOfModuleProvider stateOfModuleProvider : registeredbeans.values())
        {
            log.info("Mapping state provider for: [{}] module.", stateOfModuleProvider.getModuleName());
            moduleStateProviders.put(stateOfModuleProvider.getModuleName(), stateOfModuleProvider);
        }
    }
}
