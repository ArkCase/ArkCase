package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmRolesPrivilegesException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sergey.kolomiets  on 6/24/15.
 */
@Controller
@RequestMapping( { "/api/v1/plugin/admin", "/api/latest/plugin/admin"} )
public class RolesPrivilegesRemoveRolesPrivileges implements RolePrivilegesConstants{
    private Logger log = LoggerFactory.getLogger(getClass());
    private RolesPrivilegesService rolesPrivilegesService;

    @RequestMapping(value = "/rolesprivileges/roles/{rolesNames}/privileges/{privilegesNames}", method = RequestMethod.DELETE, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })

    @ResponseBody
    public String removeRolesPrivileges(
            @PathVariable(PROP_ROLES_NAMES) String rolesNames,
            @PathVariable(PROP_PRIVILEGES_NAMES) String privilegesNames) throws IOException, AcmRolesPrivilegesException{

        try {
            List<String> roles = Arrays.asList(rolesNames.split(","));
            List<String> privileges = Arrays.asList(privilegesNames.split(","));
            rolesPrivilegesService.removeRolesPrivileges(roles, privileges);
            return "{}";
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Can't remove roles privieges ", e);
            }
            throw new AcmRolesPrivilegesException("Can't remove roles privileges", e);
        }
    }

    public void setRolesPrivilegesService(RolesPrivilegesService rolesPrivilegesService) {
        this.rolesPrivilegesService = rolesPrivilegesService;
    }
}