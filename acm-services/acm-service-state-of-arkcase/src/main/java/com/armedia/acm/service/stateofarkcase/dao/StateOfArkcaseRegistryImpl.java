package com.armedia.acm.service.stateofarkcase.dao;

/*-
 * #%L
 * ACM Service: State of Arkcase
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

import com.armedia.acm.service.stateofarkcase.interfaces.StateOfModuleProvider;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StateOfArkcaseRegistryImpl implements StateOfArkcaseRegistry, ApplicationListener<ContextRefreshedEvent>
{
    private transient final Logger log = LogManager.getLogger(getClass());
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
        if (((ApplicationContext) event.getSource()).getParent() == null)
        {
            // after application is started, i.e. context is loaded, all the registered modules which provide state will
            // be collected
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
}
