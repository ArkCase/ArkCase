package com.armedia.acm.services.functionalaccess.web.api;

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.services.functionalaccess.service.FunctionalAccessService;

import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @author riste.tutureski
 *
 */
@Controller
@RequestMapping({ "/api/v1/functionalaccess", "/api/latest/functionalaccess" })
public class GetApplicationRolesToGroupsAPIController
{

    private Logger LOG = LoggerFactory.getLogger(getClass());
    private FunctionalAccessService functionalAccessService;

    @RequestMapping(value = "/rolestogroups", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, List<String>> getApplicationRolesToGroups(Authentication auth)
    {
        LOG.debug("Taking application roles to groups ...");

        Map<String, List<String>> retval = getFunctionalAccessService().getApplicationRolesToGroups();
        LOG.debug("Application roles to groups: " + retval.toString());

        return retval;
    }

    @RequestMapping(value = "/{roleName:.+}/groups", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<String> findApplicationGroupsByRole(Authentication auth,
            @PathVariable(value = "roleName") String roleName,
            @RequestParam(value = "authorized") Boolean authorized,
            @RequestParam(value = "dir", required = false, defaultValue = "name_lcs ASC") String sortDirection,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "1000") int maxRows) throws MuleException, AcmEncryptionException
    {
        LOG.debug("Taking application role: " + roleName + " to groups ...");

        List<String> retval = getFunctionalAccessService().findApplicationGroupsByRole(auth, roleName, startRow, maxRows, sortDirection,
                authorized);

        LOG.debug("Application role: " + roleName + " to groups number: " + retval.size());

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
