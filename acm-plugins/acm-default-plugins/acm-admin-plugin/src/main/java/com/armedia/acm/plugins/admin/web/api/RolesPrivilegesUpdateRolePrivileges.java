package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmRolesPrivilegesException;
import com.armedia.acm.plugins.admin.model.RolePrivilegesConstants;
import com.armedia.acm.plugins.admin.service.RolesPrivilegesService;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sergey.kolomiets on 6/2/15.
 */
@Controller
@RequestMapping({ "/api/v1/plugin/admin", "/api/latest/plugin/admin" })
public class RolesPrivilegesUpdateRolePrivileges implements RolePrivilegesConstants
{
    private final String PROP_PRIVILEGES = "privileges";
    private Logger log = LoggerFactory.getLogger(getClass());
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