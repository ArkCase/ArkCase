package com.armedia.acm.pluginmanager.web;

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

import com.armedia.acm.core.AcmApplication;
import com.armedia.acm.core.AcmUserAction;
import com.armedia.acm.pluginmanager.model.AcmPluginConfig;
import com.armedia.acm.pluginmanager.service.AcmConfigurablePluginsManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Controller
@RequestMapping({ "/api/v1/plugins", "/api/latest/plugins" })
public class AcmPluginController
{
    private Logger log = LogManager.getLogger(getClass());

    private AcmConfigurablePluginsManager configurablePluginsManager;

    /**
     * REST service to get the list of accessible navigator tabs. It is NOT used by the ACM webapp (the webapp uses
     * JSTIL to iterate over the AcmApplication which is added to the user session at login time). This service is to
     * ensure the tab list is available via REST... since all data that appears in the UI should be REST-accessible.
     */
    @RequestMapping(value = "/navigatorPlugins", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<AcmUserAction> enabledNavigatorPlugins(
            HttpSession userSession)
    {
        Map<String, Boolean> userPrivileges = (Map<String, Boolean>) userSession.getAttribute("acm_privileges");
        AcmApplication acmApplication = (AcmApplication) userSession.getAttribute("acm_application");

        log.debug("User Privileges is null? {}", (userPrivileges == null));
        log.debug("ACM App is null? {}", (acmApplication == null));

        if (userPrivileges == null || acmApplication == null)
        {
            throw new IllegalStateException("Invalid ACM session: no user privileges set");
        }

        List<AcmUserAction> tabs = acmApplication.getNavigatorTabs();

        Predicate<AcmUserAction> userHasPrivilege = action -> {
            String requiredPrivilege = action.getRequiredPrivilege();
            return userPrivileges.containsKey(requiredPrivilege)
                    && userPrivileges.get(requiredPrivilege);
        };

        return tabs.stream()
                .filter(userHasPrivilege)
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/configurablePlugins", method = RequestMethod.GET)
    public @ResponseBody Map<String, AcmPluginConfig> getConfigurablePlugins()
    {
        return configurablePluginsManager.getConfigurablePlugins();
    }

    public void setConfigurablePluginsManager(AcmConfigurablePluginsManager configurablePluginsManager)
    {
        this.configurablePluginsManager = configurablePluginsManager;
    }
}
