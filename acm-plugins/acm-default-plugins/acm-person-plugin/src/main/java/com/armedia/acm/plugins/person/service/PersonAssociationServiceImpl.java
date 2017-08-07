package com.armedia.acm.plugins.person.service;


import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.objectonverter.AcmMarshaller;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.person.dao.PersonAssociationDao;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.plugins.person.model.PersonOrganizationConstants;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SolrJoinDocumentsServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.TransactionException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class PersonAssociationServiceImpl implements PersonAssociationService
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private SavePersonAssociationTransaction personAssociationTransaction;

    private PersonAssociationDao personAssociationDao;

    private PersonAssociationEventPublisher personAssociationEventPublisher;

    SolrJoinDocumentsServiceImpl solrJoinDocumentsService;

    @Override
    public PersonAssociation savePersonAssociation(PersonAssociation personAssociation, Authentication authentication)
            throws AcmCreateObjectFailedException
    {
        Long id = personAssociation.getId();
        String personAssociationHistory = null;

        if (id != null)
        {
            PersonAssociation exPersonAssociation = getPersonAssociationDao().find(id);
            AcmMarshaller marshaller = ObjectConverter.createJSONMarshaller();
            // keep copy from the existing object to compare with the updated one
            // otherwise JPA will update all references and no changes can be detected
            personAssociationHistory = marshaller.marshal(exPersonAssociation);
        }

        try
        {
            PersonAssociation savedPersonAssociation = getPersonAssociationTransaction()
                    .savePersonAsssociation(personAssociation, authentication);
            getPersonAssociationEventPublisher().publishPersonAssociationEvent(personAssociationHistory, savedPersonAssociation, true);
            return savedPersonAssociation;
        } catch (MuleException | TransactionException e)
        {
            getPersonAssociationEventPublisher().publishPersonAssociationEvent(personAssociationHistory, personAssociation, false);
            throw new AcmCreateObjectFailedException("personAssociation", e.getMessage(), e);
        }
    }

    @Override
    public String getPersonAssociations(Long personId, String parentType, int start, int limit, String sort, Authentication auth) throws AcmObjectNotFoundException
    {
        return solrJoinDocumentsService.getJoinedDocuments(
                personId, "child_id_s",
                PersonOrganizationConstants.PERSON_OBJECT_TYPE, "child_type_s",
                PersonOrganizationConstants.PERSON_ASSOCIATION_OBJECT_TYPE,
                parentType, "parent_type_s",
                "parent_object",
                start, limit, sort, auth,
                "parent_ref_s", "id");
    }

    @Override
    public PersonAssociation getPersonAssociation(Long id, Authentication auth)
    {
        return personAssociationDao.find(id);
    }

    /**
     * Delete Person association
     *
     * @param id   person association id
     * @param auth Authentication
     */
    @Override
    public void deletePersonAssociation(Long id, Authentication auth)
    {
        PersonAssociation pa = personAssociationDao.find(id);
        personAssociationDao.deletePersonAssociationById(id);
        getPersonAssociationEventPublisher().publishPersonAssociationDeletedEvent(pa);
    }

    public SavePersonAssociationTransaction getPersonAssociationTransaction()
    {
        return personAssociationTransaction;
    }

    public void setPersonAssociationTransaction(SavePersonAssociationTransaction personAssociationTransaction)
    {
        this.personAssociationTransaction = personAssociationTransaction;
    }

    public PersonAssociationDao getPersonAssociationDao()
    {
        return personAssociationDao;
    }

    public void setPersonAssociationDao(PersonAssociationDao personAssociationDao)
    {
        this.personAssociationDao = personAssociationDao;
    }

    public PersonAssociationEventPublisher getPersonAssociationEventPublisher()
    {
        return personAssociationEventPublisher;
    }

    public void setPersonAssociationEventPublisher(PersonAssociationEventPublisher personAssociationEventPublisher)
    {
        this.personAssociationEventPublisher = personAssociationEventPublisher;
    }

    public void setSolrJoinDocumentsService(SolrJoinDocumentsServiceImpl solrJoinDocumentsService)
    {
        this.solrJoinDocumentsService = solrJoinDocumentsService;
    }
}
