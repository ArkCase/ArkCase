package com.armedia.acm.services.functionalaccess.web.api;

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.services.functionalaccess.service.FunctionalAccessService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author riste.tutureski
 *
 */
@Controller
@RequestMapping({ "/api/v1/functionalaccess", "/api/latest/functionalaccess" })
public class SaveApplicationRolesToGroupsAPIController
{

    private Logger LOG = LoggerFactory.getLogger(getClass());
    private FunctionalAccessService functionalAccessService;

    @RequestMapping(value = "/rolestogroups", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public boolean saveApplicationRolesToGroups(@RequestBody Map<String, List<String>> applicationRolesToGroups, Authentication auth)
    {
        LOG.debug("Saving application roles to groups ...");

        boolean retval = getFunctionalAccessService().saveApplicationRolesToGroups(applicationRolesToGroups, auth);
        LOG.debug("Successfuly save ? " + retval);

        return retval;
    }

    @RequestMapping(value = "/{roleName:.+}/groups", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public boolean addGroupsToApplicationRole(@PathVariable(value = "roleName") String roleName, @RequestBody List<String> groups,
            Authentication auth) throws AcmEncryptionException
    {
        LOG.debug("Adding groups to an application role [{}]", roleName);

        boolean retval = getFunctionalAccessService().saveGroupsToApplicationRole(groups, roleName, auth);

        LOG.debug("Successfuly save ? " + retval);

        return retval;
    }

    @RequestMapping(value = "/{roleName:.+}/groups", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public boolean deleteGroupsFromApplicationRole(@PathVariable(value = "roleName") String roleName, @RequestBody List<String> groups,
            Authentication auth)
    {
        LOG.debug("Deleting groups from an application role [{}]", roleName);

        boolean retval = getFunctionalAccessService().removeGroupsToApplicationRole(groups, roleName, auth);

        LOG.debug("Successfuly deleted ? " + retval);

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
