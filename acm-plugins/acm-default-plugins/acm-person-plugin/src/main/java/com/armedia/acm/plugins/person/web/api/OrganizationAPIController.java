package com.armedia.acm.plugins.person.web.api;

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUpdateObjectFailedException;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.service.OrganizationEventPublisher;
import com.armedia.acm.plugins.person.service.OrganizationService;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
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

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpSession;

import java.util.Date;

@Controller
@RequestMapping(value = {
        "/api/v1/plugin/organizations",
        "/api/latest/plugin/organizations" })
public class OrganizationAPIController
{

    private Logger log = LoggerFactory.getLogger(getClass());

    private OrganizationService organizationService;
    private ExecuteSolrQuery executeSolrQuery;
    private OrganizationEventPublisher organizationEventPublisher;

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
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

            saved = organizationService.saveOrganization(in, auth, ipAddress);
            organizationEventPublisher.publishOrganizationUpsertEvent(saved, isNew, true);
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

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Organization getOrganization(@PathVariable("id") Long organizationId) throws AcmObjectNotFoundException
    {
        try
        {
            Organization organization = organizationService.getOrganization(organizationId);
            getOrganizationEventPublisher().publishOrganizationViewedEvent(organization, true);
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

    public void setOrganizationService(OrganizationService organizationService)
    {
        this.organizationService = organizationService;
    }

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery)
    {
        this.executeSolrQuery = executeSolrQuery;
    }

    public OrganizationEventPublisher getOrganizationEventPublisher()
    {
        return organizationEventPublisher;
    }

    public void setOrganizationEventPublisher(OrganizationEventPublisher organizationEventPublisher)
    {
        this.organizationEventPublisher = organizationEventPublisher;
    }
}
