package com.armedia.acm.plugins.person.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.service.OrganizationService;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
@RequestMapping(value = {"/api/v1/plugin/organizations", "/api/latest/plugin/organizations"})
public class OrganizationAPIController
{

    private Logger log = LoggerFactory.getLogger(getClass());

    private OrganizationService organizationService;
    private ExecuteSolrQuery executeSolrQuery;

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Organization upsertOrganization(
            @RequestBody Organization in,
            Authentication auth
    ) throws AcmCreateObjectFailedException
    {

        log.debug("Persist a Organization: [{}];", in);

        Organization saved = organizationService.saveOrganization(in);

        return saved;

    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String
    getOrganizations(Authentication auth,
                     @RequestParam(value = "start", required = false, defaultValue = "0") int start,
                     @RequestParam(value = "n", required = false, defaultValue = "10") int n,
                     @RequestParam(value = "s", required = false, defaultValue = "ASC") String s) throws AcmObjectNotFoundException
    {
        String query = String.format("object_type_s:ORGANIZATION AND -parent_id_s:*&sort=title_parseable %s", s);
        try
        {
            return executeSolrQuery.getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, query, start, n, "");

        } catch (MuleException e)
        {
            log.error("Error while executing Solr query: {}", query, e);
            throw new AcmObjectNotFoundException("Organization", null, "Could not retrieve organizations.", e);
        }

    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Organization
    getOrganization(Authentication auth,
                    @PathVariable("id") Long organizationId) throws AcmObjectNotFoundException
    {
        try
        {
            return organizationService.getOrganization(organizationId);
        } catch (Exception e)
        {
            log.error("Error while retrieving Organization with id: [{}]", organizationId, e);
            throw new AcmObjectNotFoundException("Organization", null, "Could not retrieve organization.", e);
        }

    }

    public void setOrganizationService(OrganizationService organizationService)
    {
        this.organizationService = organizationService;
    }

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery)
    {
        this.executeSolrQuery = executeSolrQuery;
    }
}
