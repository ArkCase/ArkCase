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

import com.armedia.acm.core.exceptions.AcmNotAuthorizedException;
import com.armedia.acm.pluginmanager.model.AcmPluginPrivilege;
import com.armedia.acm.pluginmanager.model.AcmPluginUrlPrivilege;
import com.armedia.acm.pluginmanager.model.ApplicationPluginPrivilegesConfig;
import com.armedia.acm.pluginmanager.service.AcmPluginManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.List;
import java.util.Map;

/**
 * Ensure the user has the required privilege to execute a plugin URL. Only URLs in the /plugin namespace are handled by
 * this interceptor.
 */
public class AcmPluginRoleBasedAccessInterceptor extends HandlerInterceptorAdapter
{
    private AcmPluginManager acmPluginManager;
    private ApplicationPluginPrivilegesConfig pluginPrivilegesConfig;

    private Logger log = LogManager.getLogger(getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws AcmNotAuthorizedException
    {
        String method = request.getMethod();
        String url = request.getServletPath();

        log.debug("Checking user privilege for url: {} {}", method, url);

        HttpSession session = request.getSession(false);

        if (session == null)
        {
            // no session yet - login must not have completed? Anyway, proceed with the next handler.
            return true;
        }

        Map<String, Boolean> userPrivileges = (Map<String, Boolean>) session.getAttribute("acm_privileges");
        if (userPrivileges == null)
        {
            // no user privileges. somehow the user is logged in and is calling a plugin URL, but does not have
            // privileges in the user session. Somehow the login success handler did not run. This is an
            // anomalous situation. Better return HTTP 403.
            throw new AcmNotAuthorizedException(request.getServletPath());
        }
        else
        {
            Map<String, Map<String, List<String>>> urlPrivileges = pluginPrivilegesConfig.getPluginPrivilegeToUrls();
            boolean hasPrivilege = determinePrivilege(method, url, userPrivileges, urlPrivileges);

            if (!hasPrivilege)
            {
                throw new AcmNotAuthorizedException(request.getServletPath());
            }
        }

        return true;
    }

    protected boolean determinePrivilege(String method, String urlToCheck, Map<String, Boolean> userPrivileges,
            Map<String, Map<String, List<String>>> urlPrivileges)
    {
        boolean hasPrivilege = false;

        for (Map.Entry<String, Map<String, List<String>>> acmPrivilegeEntry : urlPrivileges.entrySet())
        {
            Map<String, List<String>> privilegeByMethod = acmPrivilegeEntry.getValue();

            for (Map.Entry<String, List<String>> privilegeByMethodEntry : privilegeByMethod.entrySet())
            {
                List<String> urls = privilegeByMethodEntry.getValue();

                for (String url : urls)
                {
                    AcmPluginUrlPrivilege urlPrivilege = createAcmPluginPrivilegeObject(acmPrivilegeEntry, privilegeByMethodEntry, url);

                    if (urlPrivilege.matches(urlToCheck, method))
                    {
                        String requiredPrivilege = urlPrivilege.getRequiredPrivilege().getPrivilegeName();
                        log.debug("Required privilege for {} {}: {}; user has privilege: {}", method, url, requiredPrivilege,
                                userPrivileges.containsKey(requiredPrivilege));
                        hasPrivilege = userPrivileges.getOrDefault(requiredPrivilege, false);
                        break;
                    }
                }
            }
        }

        return hasPrivilege;
    }

    private AcmPluginUrlPrivilege createAcmPluginPrivilegeObject(Map.Entry<String, Map<String, List<String>>> acmPrivilegeEntry,
            Map.Entry<String, List<String>> privilegeByMethodEntry, String url)
    {
        AcmPluginUrlPrivilege urlPrivilege = new AcmPluginUrlPrivilege();

        AcmPluginPrivilege plgPrivilege = new AcmPluginPrivilege();
        plgPrivilege.setPrivilegeName(acmPrivilegeEntry.getKey());

        urlPrivilege.setUrl(url);
        urlPrivilege.setHttpMethod(HttpMethod.resolve(privilegeByMethodEntry.getKey()));
        urlPrivilege.setRequiredPrivilege(plgPrivilege);

        return urlPrivilege;
    }

    public void setAcmPluginManager(AcmPluginManager acmPluginManager)
    {
        this.acmPluginManager = acmPluginManager;
    }

    public void setPluginPrivilegesConfig(ApplicationPluginPrivilegesConfig pluginPrivilegesConfig)
    {
        this.pluginPrivilegesConfig = pluginPrivilegesConfig;
    }
}
