package com.armedia.acm.plugins.person.service;


import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import org.springframework.security.core.Authentication;

public interface PersonAssociationService
{
    PersonAssociation savePersonAssociation(PersonAssociation personAssociation, Authentication authentication)
            throws AcmCreateObjectFailedException;

    /**
     * get associations for person including solr documents for parent
     *
     * @param personId   Person id
     * @param parentType Parent type
     * @param start      used for paging, from which row to start
     * @param limit      used for paging, how many rows to return
     * @param sort       for which field sorting should be done, default is id
     * @param auth       Authentication
     * @return JSON String of solr response
     */
    String getPersonAssociations(Long personId, String parentType, int start, int limit, String sort, Authentication auth) throws AcmObjectNotFoundException;

    /**
     * Get Person association
     *
     * @param id   person association id
     * @param auth Authentication
     * @return PersonAssociation
     */
    PersonAssociation getPersonAssociation(Long id, Authentication auth);

    /**
     * Delete Person association
     *
     * @param id   person association id
     * @param auth Authentication
     */
    void deletePersonAssociation(Long id, Authentication auth);
}
