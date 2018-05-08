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

import com.armedia.acm.plugins.admin.exception.AcmRolesPrivilegesException;
import com.armedia.acm.plugins.admin.service.RolesPrivilegesService;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Map;

/**
 * Created by sergey.kolomiets on 6/2/15.
 */
@Controller
@RequestMapping({ "/api/v1/plugin/admin", "/api/latest/plugin/admin" })
public class RolesPrivilegesRetrievePrivileges
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private RolesPrivilegesService rolesPrivilegesService;

    @RequestMapping(value = "/rolesprivileges/privileges", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public String retrievePrivileges() throws IOException, AcmRolesPrivilegesException
    {

        try
        {
            JSONObject jsonPrivileges = new JSONObject(rolesPrivilegesService.retrievePrivileges());
            return jsonPrivileges.toString();
        }
        catch (Exception e)
        {
            log.error("Can't retrieve privileges", e);
            throw new AcmRolesPrivilegesException("Can't retrieve privileges", e);
        }
    }

    @RequestMapping(value = "/{roleName:.+}/privileges", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public Map<String, String> findPrivilegesByRolePaged(
            @PathVariable(value = "roleName") String roleName,
            @RequestParam(value = "authorized") Boolean authorized,
            @RequestParam(value = "dir", required = false, defaultValue = "name_lcs ASC") String sortDirection,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "1000") int maxRows) throws IOException, AcmRolesPrivilegesException
    {
        try
        {
            return rolesPrivilegesService.getPrivilegesByRolePaged(roleName, sortDirection, startRow, maxRows, authorized);
        }
        catch (Exception e)
        {
            log.error("Can't retrieve privileges", e);
            throw new AcmRolesPrivilegesException("Can't retrieve privileges", e);
        }
    }

    @RequestMapping(value = "/{roleName:.+}/privileges", params = { "fq" }, method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public Map<String, String> findPrivilegesByRole(
            @PathVariable(value = "roleName") String roleName,
            @RequestParam(value = "authorized") Boolean authorized,
            @RequestParam(value = "fq") String filterQuery,
            @RequestParam(value = "dir", required = false, defaultValue = "name_lcs ASC") String sortDirection,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "1000") int maxRows) throws IOException, AcmRolesPrivilegesException
    {
        try
        {
            return rolesPrivilegesService.getPrivilegesByRole(roleName, authorized, filterQuery, sortDirection, startRow, maxRows);
        }
        catch (Exception e)
        {
            log.error("Can't retrieve privileges", e);
            throw new AcmRolesPrivilegesException("Can't retrieve privileges", e);
        }
    }

    public void setRolesPrivilegesService(RolesPrivilegesService rolesPrivilegesService)
    {
        this.rolesPrivilegesService = rolesPrivilegesService;
    }
}
