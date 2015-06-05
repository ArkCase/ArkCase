package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmRolesPrivilegesException;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Created by sergey.kolomiets  on 6/2/15.
 */
@Controller
@RequestMapping( { "/api/v1/plugin/admin", "/api/latest/plugin/admin"} )
public class RolesPrivilegesRetrieveRoles  {
    private Logger log = LoggerFactory.getLogger(getClass());

    private RolesPrivilegesService rolesPrivilegesService;

    @RequestMapping(value = "/rolesprivileges/roles", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })

    @ResponseBody
    public String retrieveRoles(
            @RequestBody String resource,
            HttpServletResponse response) throws IOException, AcmRolesPrivilegesException{

        try {
            JSONArray jsonRoles = new JSONArray(rolesPrivilegesService.retrieveRoles());
            return jsonRoles.toString();
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Can't retrieve roles", e);
            }
            throw new AcmRolesPrivilegesException("Can't retrieve roles", e);
        }
    }

    public void setRolesPrivilegesService(RolesPrivilegesService rolesPrivilegesService) {
        this.rolesPrivilegesService = rolesPrivilegesService;
    }
}