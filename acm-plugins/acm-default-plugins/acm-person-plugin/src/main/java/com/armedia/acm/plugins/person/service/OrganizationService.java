package com.armedia.acm.plugins.person.service;


import com.armedia.acm.plugins.person.model.Organization;

public interface OrganizationService
{
    Organization findOrCreateOrganization(String organizationName, String userId);

    Organization saveOrganization(Organization organization);

    Organization getOrganization(Long organizationId);
}
