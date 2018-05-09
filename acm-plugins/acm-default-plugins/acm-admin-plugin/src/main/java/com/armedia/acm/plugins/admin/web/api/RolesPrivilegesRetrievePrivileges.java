package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmRolesPrivilegesException;
import com.armedia.acm.plugins.admin.model.PrivilegeItem;
import com.armedia.acm.plugins.admin.service.RolesPrivilegesService;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

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
    public List<PrivilegeItem> findPrivilegesByRolePaged(
            @PathVariable(value = "roleName") String roleName,
            @RequestParam(value = "authorized") Boolean authorized,
            @RequestParam(value = "dir", required = false, defaultValue = "name_lcs ASC") String sortDirection,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "1000") int maxRows) throws AcmRolesPrivilegesException
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

    @RequestMapping(value = "/{roleName:.+}/privileges", params = { "fn" }, method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public List<PrivilegeItem> findPrivilegesByRole(
            @PathVariable(value = "roleName") String roleName,
            @RequestParam(value = "authorized") Boolean authorized,
            @RequestParam(value = "fn") String filterName,
            @RequestParam(value = "dir", required = false, defaultValue = "name_lcs ASC") String sortDirection,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "1000") int maxRows) throws AcmRolesPrivilegesException
    {
        try
        {
            return rolesPrivilegesService.getPrivilegesByRole(roleName, authorized, filterName, sortDirection, startRow, maxRows);
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