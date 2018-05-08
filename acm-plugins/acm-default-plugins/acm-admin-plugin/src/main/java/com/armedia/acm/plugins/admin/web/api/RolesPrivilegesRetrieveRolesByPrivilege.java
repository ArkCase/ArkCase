package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmRolesPrivilegesException;
import com.armedia.acm.plugins.admin.model.RolePrivilegesConstants;
import com.armedia.acm.plugins.admin.service.RolesPrivilegesService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * Created by sergey.kolomiets on 7/8/15.
 */
@Controller
@RequestMapping({ "/api/v1/plugin/admin", "/api/latest/plugin/admin" })
public class RolesPrivilegesRetrieveRolesByPrivilege implements RolePrivilegesConstants
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private RolesPrivilegesService rolesPrivilegesService;

    @RequestMapping(value = "/rolesprivileges/privileges/{privilegeName}/roles", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })

    @ResponseBody
    public List<String> retrieveRoles(
            @PathVariable(PROP_PRIVILEGE_NAME) String privilegeName) throws IOException, AcmRolesPrivilegesException
    {

        try
        {
            return rolesPrivilegesService.retrieveRolesByPrivilege(privilegeName);
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
        List<String> rolesByNamePaged = null;
        try
        {
            rolesByNamePaged = rolesPrivilegesService.getRolesByNamePaged(privilegeName, sortBy, sortDirection, startRow, maxRows,
                    authorized, "");
        }
        catch (AcmRolesPrivilegesException e)
        {
            log.warn("Can't retrieve roles {}", e);
        }

        return rolesByNamePaged;
    }

    @RequestMapping(value = "/rolesprivileges/{privilegeName:.+}/roles", params = { "fq" }, method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public List<String> findRolesByPrivilegeByName(
            @PathVariable(PROP_PRIVILEGE_NAME) String privilegeName,
            @RequestParam(value = "authorized") Boolean authorized,
            @RequestParam(value = "fq") String filterQuery,
            @RequestParam(value = "sortBy", required = false, defaultValue = "widgetName") String sortBy,
            @RequestParam(value = "dir", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "1000") int maxRows, Authentication authentication)
            throws IOException, AcmRolesPrivilegesException
    {
        try
        {
            return rolesPrivilegesService.getRolesByNamePaged(privilegeName, sortBy, sortDirection, startRow, maxRows, authorized,
                    filterQuery);
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
}