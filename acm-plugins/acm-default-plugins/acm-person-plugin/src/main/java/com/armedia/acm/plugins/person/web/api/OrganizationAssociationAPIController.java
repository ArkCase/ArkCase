package com.armedia.acm.plugins.person.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.person.model.OrganizationAssociation;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.plugins.person.service.OrganizationAssociationService;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import org.apache.commons.lang3.StringUtils;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping({"/api/v1/plugin/organizationAssociation",
        "/api/latest/plugin/organizationAssociation",
        "/api/v1/plugin/organization-associations",
        "/api/latest/plugin/organization-associations"
})
public class OrganizationAssociationAPIController
{

    private Logger log = LoggerFactory.getLogger(getClass());

    private OrganizationAssociationService organizationAssociationService;

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public OrganizationAssociation addOrganizationAssociation(
            @RequestBody OrganizationAssociation in,
            Authentication auth
    ) throws AcmCreateObjectFailedException
    {
        log.trace("Got a organizationAssociation: {}; organization association ID: '{}'", in, in.getId());
        log.trace("organizationAssociation parentType: {}", in.getParentType());

        return getOrganizationAssociationService().saveOrganizationAssociation(in, auth);

    }


    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String
    getChildObjects(Authentication auth,
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
    public OrganizationAssociation
    getOrganizationAssociation(Authentication auth,
                         @PathVariable Long id) throws AcmObjectNotFoundException
    {
        return getOrganizationAssociationService().getOrganizationAssociation(id, auth);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void
    deleteOrganizationAssociation(Authentication auth,
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
