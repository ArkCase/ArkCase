package com.armedia.acm.services.functionalaccess.web.api;

/*-
 * #%L
 * ACM Service: Functional Access Control
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.services.functionalaccess.service.FunctionalAccessService;
import com.armedia.acm.services.search.exception.SolrException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Base64;
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

    private Logger LOG = LogManager.getLogger(getClass());
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
    public List<String> findGroupsByRole(Authentication auth,
            @PathVariable(value = "roleName") String roleName,
            @RequestParam(value = "authorized") Boolean authorized,
            @RequestParam(value = "dir", required = false, defaultValue = "name_lcs ASC") String sortDirection,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "1000") int maxRows) throws SolrException
    {
        roleName = new String(Base64.getUrlDecoder().decode(roleName.getBytes()));
        LOG.debug("Taking application to groups by role name {}: ", roleName);

        List<String> retval = getFunctionalAccessService().getGroupsByRolePaged(auth, roleName, startRow, maxRows, sortDirection,
                authorized);

        LOG.debug("Application groups number {} by role name {} ", retval.size(), roleName);

        return retval;
    }

    @RequestMapping(value = "/{roleName:.+}/groups", params = {
            "fq" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<String> findGroupsByRoleByName(Authentication auth,
            @PathVariable(value = "roleName") String roleName,
            @RequestParam(value = "authorized") Boolean authorized,
            @RequestParam(value = "fq") String filterQuery,
            @RequestParam(value = "dir", required = false, defaultValue = "name_lcs ASC") String sortDirection,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "1000") int maxRows) throws SolrException
    {
        LOG.debug("Taking application to groups by role name {}: ", roleName);

        List<String> retval = getFunctionalAccessService().getGroupsByRoleByName(auth, roleName, startRow, maxRows, sortDirection,
                authorized, filterQuery);

        LOG.debug("Application groups number {} by role name {} ", retval.size(), roleName);

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
