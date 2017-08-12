package com.armedia.acm.plugins.person.service;


import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.person.model.OrganizationAssociation;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

public interface OrganizationAssociationService
{
    @Transactional
    OrganizationAssociation saveOrganizationAssociation(OrganizationAssociation organizationAssociation,
                                                        Authentication authentication)
            throws AcmCreateObjectFailedException;

    /**
     * Get Organization association
     *
     * @param id   organization association id
     * @param auth Authentication
     * @return OrganizationAssociation
     */
    OrganizationAssociation getOrganizationAssociation(Long id, Authentication auth);

    /**
     * Delete Organization association
     *
     * @param id   organization association id
     * @param auth Authentication
     */
    void deleteOrganizationAssociation(Long id, Authentication auth);

    /**
     * get associations for organization including solr documents for parent
     *
     * @param organizationId Organization id
     * @param parentType     Parent type
     * @param start          used for paging, from which row to start
     * @param limit          used for paging, how many rows to return
     * @param sort           for which field sorting should be done, default is id
     * @param auth           Authentication
     * @return JSON String of solr response
     */
    String getOrganizationAssociations(Long organizationId, String parentType, int start, int limit, String sort, Authentication auth) throws AcmObjectNotFoundException;
}
