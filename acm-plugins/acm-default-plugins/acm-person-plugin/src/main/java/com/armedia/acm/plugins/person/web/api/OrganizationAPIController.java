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

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUpdateObjectFailedException;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.service.OrganizationEventPublisher;
import com.armedia.acm.plugins.person.service.OrganizationService;
import com.armedia.acm.services.participants.model.DecoratedAssignedObjectParticipants;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;

import org.mule.api.MuleException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpSession;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = { "/api/v1/plugin/organizations", "/api/latest/plugin/organizations" })
public class OrganizationAPIController
{

    private Logger log = LogManager.getLogger(getClass());

    private OrganizationService organizationService;
    private ExecuteSolrQuery executeSolrQuery;
    private OrganizationEventPublisher organizationEventPublisher;
    private String facetedSearchPath;
    private ObjectConverter objectConverter;

    @PreAuthorize("#in.organizationId == null or hasPermission(#in.organizationId, 'ORGANIZATION', 'editOrganization')")
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @DecoratedAssignedObjectParticipants
    @ResponseBody
    public Organization upsertOrganization(@RequestBody Organization in, Authentication auth, HttpSession httpSession)
            throws AcmCreateObjectFailedException, AcmUpdateObjectFailedException
    {

        log.debug("Persist a Organization: [{}];", in);
        String ipAddress = (String) httpSession.getAttribute("acm_ip_address");
        Organization saved;
        try
        {
            boolean isNew = in.getId() == null;
            // explicitly set modifier and modified to trigger transformer to reindex data
            // fixes problem when some child objects are changed (e.g participants) and solr document is not updated
            in.setModifier(AuthenticationUtils.getUsername());
            in.setModified(new Date());
            Organization oldOrganization = null;
            if (!isNew)
            {
                String old = getObjectConverter().getJsonMarshaller().marshal(organizationService.getOrganization(in.getId()));
                oldOrganization = getObjectConverter().getJsonUnmarshaller().unmarshall(old, Organization.class);
            }
            saved = organizationService.saveOrganization(in, auth, ipAddress);
            organizationEventPublisher.publishOrganizationUpsertEvent(saved, oldOrganization, isNew, true);
            return saved;
        }
        catch (PipelineProcessException | PersistenceException e)
        {
            log.error("Error while saving Organization: [{}]", in, e);
            throw new AcmCreateObjectFailedException("Organization", e.getMessage(), e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getOrganizations(Authentication auth, @RequestParam(value = "start", required = false, defaultValue = "0") int start,
            @RequestParam(value = "n", required = false, defaultValue = "10") int n,
            @RequestParam(value = "s", required = false, defaultValue = "ASC") String s) throws AcmObjectNotFoundException
    {
        String query = String.format("object_type_s:ORGANIZATION AND -parent_id_s:*&sort=title_parseable %s", s);
        try
        {
            return executeSolrQuery.getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, query, start, n, "");

        }
        catch (MuleException e)
        {
            log.error("Error while executing Solr query: {}", query, e);
            throw new AcmObjectNotFoundException("Organization", null, "Could not retrieve organizations.", e);
        }

    }

    @PreAuthorize("hasPermission(#organizationId, 'ORGANIZATION', 'viewOrganizationPage')")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @DecoratedAssignedObjectParticipants
    @ResponseBody
    public Organization getOrganization(@PathVariable("id") Long organizationId) throws AcmObjectNotFoundException
    {
        try
        {
            Organization organization = organizationService.getOrganization(organizationId);
            organizationEventPublisher.publishOrganizationViewedEvent(organization, true);
            return organization;
        }
        catch (Exception e)
        {
            log.error("Error while retrieving Organization with id: [{}]", organizationId, e);
            throw new AcmObjectNotFoundException("Organization", null, "Could not retrieve organization.", e);
        }

    }

    @RequestMapping(value = "/{organizationId}/associations/{objectType}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getChildObjects(Authentication auth, @PathVariable("organizationId") Long organizationId,
            @PathVariable("objectType") String objectType, @RequestParam(value = "start", required = false, defaultValue = "0") int start,
            @RequestParam(value = "n", required = false, defaultValue = "10") int n) throws AcmObjectNotFoundException
    {
        String query = String.format(
                "{!join from=parent_ref_s to=id}object_type_s:ORGANIZATION-ASSOCIATION AND parent_type_s:%s AND child_id_s:%s", objectType,
                organizationId.toString());
        try
        {
            return executeSolrQuery.getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, query, start, n, "");
        }
        catch (MuleException e)
        {
            log.error("Error while executing Solr query: {}", query, e);
            throw new AcmObjectNotFoundException("Organization", null,
                    String.format("Could not retrieve %s for organization id[%s]", objectType, organizationId), e);
        }
    }

    @RequestMapping(value = "/search/{organizationId}", method = RequestMethod.GET)
    public String searchOrganizations(@PathVariable("organizationId") Long organizationId) throws UnsupportedEncodingException
    {
        Organization organization = organizationService.getOrganization(organizationId);

        List<String> filteredOrganizations = new ArrayList<>();
        filteredOrganizations.add(Long.toString(organization.getOrganizationId()));

        Organization parent = organization.getParentOrganization();
        while (parent != null)
        {
            String parentId = Long.toString(parent.getOrganizationId());
            if (filteredOrganizations.contains(parentId))
            {
                break;
            }
            filteredOrganizations.add(parentId);
            parent = parent.getParentOrganization();
        }

        String organizationFilter = URLEncoder.encode(
                filteredOrganizations.stream().map(o -> String.format("fq=\"-object_id_s\":%s", o)).collect(Collectors.joining("&")),
                "UTF-8");

        return String.format(facetedSearchPath, organizationFilter);
    }

    public void setOrganizationService(OrganizationService organizationService)
    {
        this.organizationService = organizationService;
    }

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery)
    {
        this.executeSolrQuery = executeSolrQuery;
    }

    public void setOrganizationEventPublisher(OrganizationEventPublisher organizationEventPublisher)
    {
        this.organizationEventPublisher = organizationEventPublisher;
    }

    /**
     * @param facetedSearchPath
     *            the facetedSearchPath to set
     */
    public void setFacetedSearchPath(String facetedSearchPath)
    {
        this.facetedSearchPath = facetedSearchPath;
    }

    public ObjectConverter getObjectConverter()
    {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
    }

}
