package com.armedia.acm.plugins.person.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.plugins.person.model.OrganizationAssociation;
import com.armedia.acm.plugins.person.service.OrganizationAssociationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({"/api/v1/plugin/organizationAssociation", "/api/latest/plugin/organizationAssociation"})
public class SaveOrganizationAssociationAPIController
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
        log.trace("Got a organizationAssociation: {}; organization ID: '{}'", in, in.getId());
        log.trace("organizationAssociation parentType: {}", in.getParentType());

        return getOrganizationAssociationService().saveOrganizationAssociation(in, auth);

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
