package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmRolesPrivilegesException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

/**
 * Created by sergey.kolomiets  on 6/24/15.
 */
@Controller
@RequestMapping( { "/api/v1/plugin/admin", "/api/latest/plugin/admin"} )
public class RolesPrivilegesUpdateRole implements RolePrivilegesConstants{
    private Logger log = LoggerFactory.getLogger(getClass());
    private RolesPrivilegesService rolesPrivilegesService;

    @RequestMapping(value = "/rolesprivileges/roles/{roleName}", method = RequestMethod.PUT, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })

    @ResponseBody
    public String createRole(
            @RequestBody String resource,
            @PathVariable(PROP_ROLE_NAME) String roleName) throws IOException, AcmRolesPrivilegesException{

        try {
            JSONObject newRoleObject = new JSONObject(resource);
            String newRoleName = newRoleObject.getString(PROP_ROLE_NAME);
            if (newRoleName == null) {
                throw new AcmRolesPrivilegesException("Role name is undefined");
            }
            rolesPrivilegesService.updateRole(roleName, newRoleName);

            return "{}";
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Can't update role", e);
            }
            throw new AcmRolesPrivilegesException("Can't update role", e);
        }
    }

    public void setRolesPrivilegesService(RolesPrivilegesService rolesPrivilegesService) {
        this.rolesPrivilegesService = rolesPrivilegesService;
    }
}