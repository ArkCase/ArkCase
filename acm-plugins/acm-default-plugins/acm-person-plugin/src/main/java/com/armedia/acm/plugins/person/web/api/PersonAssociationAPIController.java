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
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.plugins.person.service.PersonAssociationService;
import com.armedia.acm.services.search.exception.SolrException;
import com.armedia.acm.services.search.model.solr.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
@RequestMapping({ "/api/v1/plugin/personAssociation",
        "/api/latest/plugin/personAssociation",
        "/api/v1/plugin/person-associations",
        "/api/latest/plugin/person-associations"
})
public class PersonAssociationAPIController
{

    private Logger log = LogManager.getLogger(getClass());

    private ExecuteSolrQuery executeSolrQuery;

    private PersonAssociationService personAssociationService;

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public PersonAssociation addPersonAssociation(
            @RequestBody PersonAssociation in,
            Authentication auth) throws AcmCreateObjectFailedException
    {
        log.trace("Got a personAssociation: {}; person association ID: '{}'", in, in.getId());
        log.trace("personAssociation parentType: {}", in.getParentType());

        return getPersonAssociationService().savePersonAssociation(in, auth);
    }

    @RequestMapping(value = "/{parentType}/{parentId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getPersonAssociationsByParent(Authentication auth,
            @PathVariable String parentType, @PathVariable Long parentId,
            @RequestParam(value = "start", required = false, defaultValue = "0") int start,
            @RequestParam(value = "n", required = false, defaultValue = "5") int n,
            @RequestParam(value = "s", required = false, defaultValue = "ASC") String s) throws AcmObjectNotFoundException
    {
        String query = String.format("object_type_s:PERSON-ASSOCIATION AND parent_type_s:%s AND parent_id_s:%s", parentType, parentId);
        try
        {
            return executeSolrQuery.getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, query, start, n, "");

        }
        catch (SolrException e)
        {
            log.error("Error while executing Solr query: {}", query, e);
            throw new AcmObjectNotFoundException("Person", null, "Could not retrieve person associations.", e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getChildObjects(Authentication auth,
            @RequestParam(value = "person-id", required = true) Long personId,
            @RequestParam(value = "parent-type", required = true) String parentType,
            @RequestParam(value = "start", required = false, defaultValue = "0") int start,
            @RequestParam(value = "n", required = false, defaultValue = "10") int n,
            @RequestParam(value = "sort", required = false, defaultValue = "id asc") String sort) throws AcmObjectNotFoundException
    {

        return personAssociationService.getPersonAssociations(personId, parentType, start, n, sort, auth);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public PersonAssociation getPersonAssociation(Authentication auth,
            @PathVariable Long id) throws AcmObjectNotFoundException
    {
        return personAssociationService.getPersonAssociation(id, auth);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deletePersonAssociation(Authentication auth,
            @PathVariable Long id) throws AcmObjectNotFoundException
    {
        personAssociationService.deletePersonAssociation(id, auth);
    }

    public PersonAssociationService getPersonAssociationService()
    {
        return personAssociationService;
    }

    public void setPersonAssociationService(PersonAssociationService personAssociationService)
    {
        this.personAssociationService = personAssociationService;
    }

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery)
    {
        this.executeSolrQuery = executeSolrQuery;
    }

}
