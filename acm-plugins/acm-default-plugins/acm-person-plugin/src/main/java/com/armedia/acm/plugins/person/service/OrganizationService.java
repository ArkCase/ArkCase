package com.armedia.acm.plugins.person.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUpdateObjectFailedException;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;

import org.springframework.security.core.Authentication;

public interface OrganizationService
{
    Organization findOrCreateOrganization(String organizationName, String userId);

    Organization saveOrganization(Organization organization, Authentication auth, String ipAddress)
            throws PipelineProcessException, AcmCreateObjectFailedException, AcmUpdateObjectFailedException;

    Organization getOrganization(Long organizationId);
}
