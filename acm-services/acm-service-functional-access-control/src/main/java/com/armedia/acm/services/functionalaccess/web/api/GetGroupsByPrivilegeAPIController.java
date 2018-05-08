package com.armedia.acm.services.functionalaccess.web.api;

/*-
 * #%L
 * ACM Service: Functional Access Control
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

import com.armedia.acm.pluginmanager.service.AcmPluginManager;
import com.armedia.acm.services.functionalaccess.service.FunctionalAccessService;
import com.armedia.acm.services.users.dao.UserDao;

import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = { "/api/v1/service/functionalaccess", "/api/latest/service/functionalaccess" })
public class GetGroupsByPrivilegeAPIController
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private AcmPluginManager pluginManager;
    private UserDao userDao;
    private FunctionalAccessService functionalAccessService;

    @RequestMapping(method = RequestMethod.GET, value = "/groups/{privilege}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String groupsByPrivilege(@PathVariable(value = "privilege") String privilege,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "50") int maxRows,
            @RequestParam(value = "s", required = false, defaultValue = "") String sort,
            Authentication auth) throws MuleException
    {
        if (log.isDebugEnabled())
        {
            log.debug("Looking for users for privilege '" + privilege);
        }

        List<String> rolesForPrivilege = getPluginManager().getRolesForPrivilege(privilege);
        Map<String, List<String>> rolesToGroups = getFunctionalAccessService().getApplicationRolesToGroups();

        String retval = getFunctionalAccessService().getGroupsByPrivilege(rolesForPrivilege, rolesToGroups, startRow, maxRows, sort, auth);

        return retval;
    }

    public AcmPluginManager getPluginManager()
    {
        return pluginManager;
    }

    public void setPluginManager(AcmPluginManager pluginManager)
    {
        this.pluginManager = pluginManager;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public FunctionalAccessService getFunctionalAccessService()
    {
        return functionalAccessService;
    }

    public void setFunctionalAccessService(
            FunctionalAccessService functionalAccessService)
    {
        this.functionalAccessService = functionalAccessService;
    }
}
