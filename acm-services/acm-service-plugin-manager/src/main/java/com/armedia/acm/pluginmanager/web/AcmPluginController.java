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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping({ "/api/v1/plugins", "/api/latest/plugins" })
public class AcmPluginController
{
    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * REST service to get the list of accessible navigator tabs. It is NOT used by the ACM webapp (the webapp uses
     * JSTIL to iterate over the AcmApplication which is added to the user session at login time). This service is to
     * ensure the tab list is available via REST... since all data that appears in the UI should be REST-accessible.
     */
    @RequestMapping(value = "/navigatorPlugins", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<AcmUserAction> enabledNavigatorPlugins(
            HttpSession userSession,
            HttpServletResponse response)
            throws IOException
    {
        Map<String, Boolean> userPrivileges = (Map<String, Boolean>) userSession.getAttribute("acm_privileges");
        AcmApplication acmApplication = (AcmApplication) userSession.getAttribute("acm_application");

        if (log.isDebugEnabled())
        {
            log.debug("User Privileges is null? " + (userPrivileges == null));
            log.debug("ACM App is null? " + (acmApplication == null));
        }

        if (userPrivileges == null || acmApplication == null)
        {
            throw new IllegalStateException("Invalid ACM session: no user privileges set");
        }

        List<AcmUserAction> tabs = acmApplication.getNavigatorTabs();
        List<AcmUserAction> userAccessibleTabs = new ArrayList<>();
        for (AcmUserAction action : tabs)
        {
            String requiredPrivilege = action.getRequiredPrivilege();
            if (userPrivileges.containsKey(requiredPrivilege) && userPrivileges.get(requiredPrivilege))
            {
                userAccessibleTabs.add(action);
            }
        }

        return userAccessibleTabs;
    }
}
