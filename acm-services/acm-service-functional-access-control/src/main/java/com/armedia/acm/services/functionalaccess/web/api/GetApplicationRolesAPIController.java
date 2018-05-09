package com.armedia.acm.services.functionalaccess.web.api;

import com.armedia.acm.services.functionalaccess.service.FunctionalAccessService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author riste.tutureski
 *
 */
@Controller
@RequestMapping({ "/api/v1/functionalaccess", "/api/latest/functionalaccess" })
public class GetApplicationRolesAPIController
{

    private Logger LOG = LoggerFactory.getLogger(getClass());
    private FunctionalAccessService functionalAccessService;

    @RequestMapping(value = "/roles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<String> getApplicationRoles(Authentication auth)
    {
        LOG.debug("Taking application roles ...");

        List<String> retval = getFunctionalAccessService().getApplicationRoles();
        LOG.debug("Application roles: " + retval.toString());

        return retval;
    }

    @RequestMapping(value = "/appRoles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<String> getApplicationRolesPaged(
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10000") int maxRows,
            @RequestParam(value = "s", required = false, defaultValue = "name_lcs") String sortBy,
            @RequestParam(value = "dir", required = false, defaultValue = "ASC") String sortDirection, Authentication auth)
    {
        LOG.debug("Taking application roles paged...");

        List<String> retval = getFunctionalAccessService().getApplicationRolesPaged(sortDirection, startRow, maxRows);
        LOG.debug("Application roles size: {}", retval.size());

        return retval;
    }

    @RequestMapping(value = "/appRoles", params = { "fn" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<String> getApplicationRolesByName(
            @RequestParam(value = "fn") String filterName,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10000") int maxRows,
            @RequestParam(value = "s", required = false, defaultValue = "name_lcs") String sortBy,
            @RequestParam(value = "dir", required = false, defaultValue = "ASC") String sortDirection, Authentication auth)
    {
        LOG.debug("Taking application roles by name...");

        List<String> retval = getFunctionalAccessService().getApplicationRolesByName(sortDirection, startRow, maxRows, filterName);
        LOG.debug("Application roles: {}", retval.toString());

        return retval;
    }

    public FunctionalAccessService getFunctionalAccessService()
    {
        return functionalAccessService;
    }

    public void setFunctionalAccessService(FunctionalAccessService functionalAccessService)
    {
        this.functionalAccessService = functionalAccessService;
    }
}
