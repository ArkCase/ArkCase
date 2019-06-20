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
import com.armedia.acm.plugins.admin.model.RolePrivilegesConstants;
import com.armedia.acm.plugins.admin.service.RolesPrivilegesService;

import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * Created by sergey.kolomiets on 6/2/15.
 */
@Controller
@RequestMapping({ "/api/v1/plugin/admin", "/api/latest/plugin/admin" })
public class RolesPrivilegesUpdateRolePrivileges implements RolePrivilegesConstants
{
    private final String PROP_PRIVILEGES = "privileges";
    private Logger log = LogManager.getLogger(getClass());
    private RolesPrivilegesService rolesPrivilegesService;

    @RequestMapping(value = "/rolesprivileges/roles/{roleName}/privileges", method = RequestMethod.PUT, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })

    @ResponseBody
    public String updateRolePrivileges(
            @RequestBody String resource,
            @PathVariable(PROP_ROLE_NAME) String roleName) throws AcmRolesPrivilegesException
    {
        try
        {
            JSONObject rolePrivilegesObject = new JSONObject(resource);
            JSONArray privelegesArray = rolePrivilegesObject.getJSONArray(PROP_PRIVILEGES);
            List<String> privileges = new ArrayList<>();
            for (int i = 0; i < privelegesArray.length(); i++)
            {
                privileges.add(privelegesArray.getString(i));
            }

            rolesPrivilegesService.updateRolePrivileges(roleName, privileges);
            return "{}";
        }
        catch (Exception e)
        {
            log.error("Can't update role [{}] privileges", roleName, e);
            throw new AcmRolesPrivilegesException(String.format("Can't update role '%s' privileges", roleName), e);
        }
    }

    @RequestMapping(value = "/rolesprivileges/{roleName}/privileges", method = RequestMethod.PUT, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public ResponseEntity<?> addPrivilegesToApplicationRole(
            @PathVariable(PROP_ROLE_NAME) String roleName,
            @RequestBody List<String> privileges) throws AcmRolesPrivilegesException
    {
        roleName = new String(Base64.getUrlDecoder().decode(roleName.getBytes()));
        try
        {
            log.debug("Adding privileges to an application role [{}]", roleName);
            rolesPrivilegesService.updateRolePrivileges(roleName, privileges);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (Exception e)
        {
            log.error("Can't update role [{}] privileges", roleName, e);
            throw new AcmRolesPrivilegesException(String.format("Can't update role '%s' privileges", roleName), e);
        }
    }

    @RequestMapping(value = "/rolesprivileges/{roleName}/privileges", method = RequestMethod.DELETE, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public ResponseEntity<?> removePrivilegesToApplicationRole(
            @PathVariable(PROP_ROLE_NAME) String roleName,
            @RequestBody List<String> privileges) throws AcmRolesPrivilegesException
    {
        roleName = new String(Base64.getUrlDecoder().decode(roleName.getBytes()));
        try
        {
            log.debug("Removing privileges from an application role [{}]", roleName);
            rolesPrivilegesService.removeRolesPrivileges(new ArrayList<>(Arrays.asList(roleName)), privileges);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (Exception e)
        {
            log.error("Can't update role [{}] privileges", roleName, e);
            throw new AcmRolesPrivilegesException(String.format("Can't update role '%s' privileges", roleName), e);
        }
    }

    public void setRolesPrivilegesService(RolesPrivilegesService rolesPrivilegesService)
    {
        this.rolesPrivilegesService = rolesPrivilegesService;
    }
}
