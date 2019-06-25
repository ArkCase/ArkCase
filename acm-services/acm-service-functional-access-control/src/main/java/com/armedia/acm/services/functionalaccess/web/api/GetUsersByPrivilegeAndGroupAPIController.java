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
import com.armedia.acm.services.functionalaccess.model.FunctionalAccessConstants;
import com.armedia.acm.services.functionalaccess.service.FunctionalAccessService;
import com.armedia.acm.services.users.model.AcmUser;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping(value = { "/api/v1/service/functionalaccess", "/api/latest/service/functionalaccess" })
public class GetUsersByPrivilegeAndGroupAPIController
{
    private Logger log = LogManager.getLogger(getClass());
    private AcmPluginManager pluginManager;
    private FunctionalAccessService functionalAccessService;

    @RequestMapping(method = RequestMethod.GET, value = "/users/{privilege}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<AcmUser> usersByPrivilege(@PathVariable(value = "privilege") String privilege)
    {
        if (log.isDebugEnabled())
        {
            log.debug("Looking for users for privilege '" + privilege);
        }

        List<AcmUser> retval = new ArrayList<>();

        List<String> rolesForPrivilege = getPluginManager().getRolesForPrivilege(privilege);
        Map<String, List<String>> rolesToGroups = getFunctionalAccessService().getApplicationRolesToGroups();

        // Creating set to avoid duplicates. AcmUser has overrided "equals" and "hasCode" methods
        Set<AcmUser> users = getFunctionalAccessService().getUsersByRolesAndGroups(rolesForPrivilege, rolesToGroups, null, null);

        retval.addAll(users);

        return retval;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/users/{privilege}/{group}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<AcmUser> usersByPrivilegeAndGroup(@PathVariable(value = "privilege") String privilege,
            @PathVariable(value = "group") String group)
    {
        if (log.isDebugEnabled())
        {
            log.debug("Looking for users for privilege '" + privilege + "' and group " + group);
        }

        List<AcmUser> retval = new ArrayList<>();

        List<String> rolesForPrivilege = getPluginManager().getRolesForPrivilege(privilege);
        Map<String, List<String>> rolesToGroups = getFunctionalAccessService().getApplicationRolesToGroups();

        // Creating set to avoid duplicates. AcmUser has overrided "equals" and "hasCode" methods
        Set<AcmUser> users = getFunctionalAccessService().getUsersByRolesAndGroups(rolesForPrivilege, rolesToGroups, group, null);

        retval.addAll(users);

        return retval;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/users/{privilege}/{group}/{currentAssignee}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<AcmUser> usersByPrivilegeAndGroupPlusCurrentAssignee(@PathVariable(value = "privilege") String privilege,
            @PathVariable(value = "group") String group,
            @PathVariable(value = "currentAssignee") String currentAssignee)
    {
        if (log.isDebugEnabled())
        {
            log.debug("Looking for users for privilege '" + privilege + "', group " + group + " plus current assignee '" + currentAssignee
                    + "'");
        }

        if (FunctionalAccessConstants.ALL_GROUPS.equals(group))
        {
            // This will avoid taking users only for specific group
            group = null;
        }

        List<AcmUser> retval = new ArrayList<>();

        List<String> rolesForPrivilege = getPluginManager().getRolesForPrivilege(privilege);
        Map<String, List<String>> rolesToGroups = getFunctionalAccessService().getApplicationRolesToGroups();

        // Creating set to avoid duplicates. AcmUser has overrided "equals" and "hasCode" methods
        Set<AcmUser> users = getFunctionalAccessService().getUsersByRolesAndGroups(rolesForPrivilege, rolesToGroups, group,
                currentAssignee);

        retval.addAll(users);

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
