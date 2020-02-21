package com.armedia.acm.plugins.admin.web.api;

/*-
 * #%L
 * ACM Default Plugin: admin
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

import com.armedia.acm.configuration.service.ConfigurationPropertyException;
import com.armedia.acm.plugins.admin.exception.AcmRolesPrivilegesException;
import com.armedia.acm.plugins.admin.model.RolePrivilegesConstants;
import com.armedia.acm.plugins.admin.service.RolesPrivilegesService;
import com.armedia.acm.services.functionalaccess.service.FunctionalAccessService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sergey.kolomiets on 7/8/15.
 */
@Controller
@RequestMapping({ "/api/v1/plugin/admin", "/api/latest/plugin/admin" })
public class RolesPrivilegesRetrieveRolesByPrivilege implements RolePrivilegesConstants
{
    private Logger log = LogManager.getLogger(getClass());

    private RolesPrivilegesService rolesPrivilegesService;
    private FunctionalAccessService functionalAccessService;

    @RequestMapping(value = "/rolesprivileges/privileges/{privilegeName}/roles", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })

    @ResponseBody
    public List<String> retrieveRoles(
            @PathVariable(PROP_PRIVILEGE_NAME) String privilegeName) throws IOException, AcmRolesPrivilegesException
    {

        try
        {
            return functionalAccessService.getRolesByPrivilege(privilegeName);
        }
        catch (Exception e)
        {
            log.error("Can't retrieve roles", e);
            throw new AcmRolesPrivilegesException("Can't retrieve roles", e);
        }
    }

    @RequestMapping(value = "/rolesprivileges/{privilegeName:.+}/roles", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public List<String> findRolesByPrivilegePaged(
            @PathVariable(PROP_PRIVILEGE_NAME) String privilegeName,
            @RequestParam(value = "authorized") Boolean authorized,
            @RequestParam(value = "sortBy", required = false, defaultValue = "widgetName") String sortBy,
            @RequestParam(value = "dir", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "1000") int maxRows, Authentication authentication)
    {
        List<String> rolesByNamePaged = new ArrayList<>();
        try
        {
            rolesByNamePaged = rolesPrivilegesService.getRolesByNamePaged(privilegeName, sortBy, sortDirection, startRow, maxRows,
                    authorized, "");
        }
        catch (ConfigurationPropertyException e)
        {
            log.warn("Can't retrieve roles {}", e);
        }

        return rolesByNamePaged;
    }

    @RequestMapping(value = "/rolesprivileges/{privilegeName:.+}/roles", params = { "fn" }, method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public List<String> findRolesByPrivilegeByName(
            @PathVariable(PROP_PRIVILEGE_NAME) String privilegeName,
            @RequestParam(value = "authorized") Boolean authorized,
            @RequestParam(value = "fn") String filterName,
            @RequestParam(value = "sortBy", required = false, defaultValue = "widgetName") String sortBy,
            @RequestParam(value = "dir", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "1000") int maxRows, Authentication authentication)
            throws IOException, AcmRolesPrivilegesException
    {
        try
        {
            return rolesPrivilegesService.getRolesByNamePaged(privilegeName, sortBy, sortDirection, startRow, maxRows, authorized,
                    filterName);
        }
        catch (Exception e)
        {
            log.error("Can't retrieve roles", e);
            throw new AcmRolesPrivilegesException("Can't retrieve roles", e);
        }
    }

    public void setRolesPrivilegesService(RolesPrivilegesService rolesPrivilegesService)
    {
        this.rolesPrivilegesService = rolesPrivilegesService;
    }

    public void setFunctionalAccessService(FunctionalAccessService functionalAccessService)
    {
        this.functionalAccessService = functionalAccessService;
    }
}
