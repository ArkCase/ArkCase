package com.armedia.acm.plugins.person.service;


import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.plugins.person.model.OrganizationAssociation;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

public interface OrganizationAssociationService
{
    @Transactional
    OrganizationAssociation saveOrganizationAssociation(OrganizationAssociation organizationAssociation,
                                                        Authentication authentication)
            throws AcmCreateObjectFailedException;
}
