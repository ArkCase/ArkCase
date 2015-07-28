package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmRolesPrivilegesException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

/**
 * Created by sergey.kolomiets  on 6/2/15.
 */
@Controller
@RequestMapping( { "/api/v1/plugin/admin", "/api/latest/plugin/admin"} )
public class RolesPrivilegesRetrieveRolePrivileges implements RolePrivilegesConstants {
    private Logger log = LoggerFactory.getLogger(getClass());

    private RolesPrivilegesService rolesPrivilegesService;

    @RequestMapping(value = "/rolesprivileges/roles/{roleName}/privileges", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })

    @ResponseBody
    public String retrieveRolePrivileges(@PathVariable(PROP_ROLE_NAME) String roleName) throws IOException, AcmRolesPrivilegesException{

        try {
            if (roleName == null) {
                throw new AcmRolesPrivilegesException("Role name is undefined");
            }
            JSONObject jsonPrivileges = new JSONObject(rolesPrivilegesService.retrieveRolePrivileges(roleName));
            return jsonPrivileges.toString();
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(String.format("Can't retrieve role '%s' privileges", roleName), e);
            }
            throw new AcmRolesPrivilegesException(String.format("Can't retrieve role '%s' privileges", roleName), e);
        }
    }

    public void setRolesPrivilegesService(RolesPrivilegesService rolesPrivilegesService) {
        this.rolesPrivilegesService = rolesPrivilegesService;
    }
}