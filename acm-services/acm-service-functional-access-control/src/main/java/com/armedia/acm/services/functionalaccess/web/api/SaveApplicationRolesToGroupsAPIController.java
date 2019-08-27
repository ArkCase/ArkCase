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

import com.armedia.acm.crypto.exceptions.AcmEncryptionException;
import com.armedia.acm.services.functionalaccess.service.FunctionalAccessService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
public class SaveApplicationRolesToGroupsAPIController
{

    private Logger LOG = LogManager.getLogger(getClass());
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
        roleName = new String(Base64.getUrlDecoder().decode(roleName.getBytes()));
        LOG.debug("Adding groups to an application role [{}]", roleName);

        boolean retval = getFunctionalAccessService().saveGroupsToApplicationRole(groups, roleName, auth);

        LOG.debug("Successfuly save ? {}", retval);

        return retval;
    }

    @RequestMapping(value = "/{roleName:.+}/groups", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public boolean deleteGroupsFromApplicationRole(@PathVariable(value = "roleName") String roleName, @RequestBody List<String> groups,
            Authentication auth)
    {
        roleName = new String(Base64.getUrlDecoder().decode(roleName.getBytes()));

        LOG.debug("Deleting groups from an application role [{}]", roleName);

        boolean retval = getFunctionalAccessService().removeGroupsToApplicationRole(groups, roleName, auth);

        LOG.debug("Successfuly deleted ? {}", retval);

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
