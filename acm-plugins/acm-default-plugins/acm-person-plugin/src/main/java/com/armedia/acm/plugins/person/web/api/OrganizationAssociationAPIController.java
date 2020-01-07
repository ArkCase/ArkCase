package com.armedia.acm.plugins.person.web.api;

/*-
 * #%L
 * ACM Default Plugin: Person
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

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.person.model.OrganizationAssociation;
import com.armedia.acm.plugins.person.service.OrganizationAssociationService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping({ "/api/v1/plugin/organizationAssociation",
        "/api/latest/plugin/organizationAssociation",
        "/api/v1/plugin/organization-associations",
        "/api/latest/plugin/organization-associations"
})
public class OrganizationAssociationAPIController
{

    private Logger log = LogManager.getLogger(getClass());

    private OrganizationAssociationService organizationAssociationService;

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public OrganizationAssociation addOrganizationAssociation(
            @RequestBody OrganizationAssociation in,
            Authentication auth) throws AcmCreateObjectFailedException
    {
        log.trace("Got a organizationAssociation: {}; organization association ID: '{}'", in, in.getId());
        log.trace("organizationAssociation parentType: {}", in.getParentType());

        return getOrganizationAssociationService().saveOrganizationAssociation(in, auth);

    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getChildObjects(Authentication auth,
            @RequestParam(value = "organization-id") Long organizationId,
            @RequestParam(value = "parent-type", required = false) String parentType,
            @RequestParam(value = "start", required = false, defaultValue = "0") int start,
            @RequestParam(value = "n", required = false, defaultValue = "10") int n,
            @RequestParam(value = "sort", required = false, defaultValue = "id asc") String sort) throws AcmObjectNotFoundException
    {
        return organizationAssociationService.getOrganizationAssociations(organizationId, parentType, start, n, sort, auth);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public OrganizationAssociation getOrganizationAssociation(Authentication auth,
            @PathVariable Long id) throws AcmObjectNotFoundException
    {
        return getOrganizationAssociationService().getOrganizationAssociation(id, auth);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteOrganizationAssociation(Authentication auth,
            @PathVariable Long id) throws AcmObjectNotFoundException
    {
        getOrganizationAssociationService().deleteOrganizationAssociation(id, auth);
    }

    public OrganizationAssociationService getOrganizationAssociationService()
    {
        return organizationAssociationService;
    }

    public void setOrganizationAssociationService(OrganizationAssociationService organizationAssociationService)
    {
        this.organizationAssociationService = organizationAssociationService;
    }
}
