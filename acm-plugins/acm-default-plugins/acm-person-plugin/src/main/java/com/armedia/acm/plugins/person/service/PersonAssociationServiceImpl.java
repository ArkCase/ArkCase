package com.armedia.acm.plugins.person.service;

/*-
 * #%L
 * ACM Default Plugin: Person
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.CHILD_ID_S;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.CHILD_TYPE_S;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_REF_S;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_TYPE_S;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.objectonverter.AcmMarshaller;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.person.dao.PersonAssociationDao;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.plugins.person.model.PersonOrganizationConstants;
import com.armedia.acm.services.search.service.SolrJoinDocumentsServiceImpl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.TransactionException;

import java.util.List;

public class PersonAssociationServiceImpl implements PersonAssociationService
{
    SolrJoinDocumentsServiceImpl solrJoinDocumentsService;
    private Logger log = LogManager.getLogger(getClass());
    private SavePersonAssociationTransaction personAssociationTransaction;
    private PersonAssociationDao personAssociationDao;
    private PersonAssociationEventPublisher personAssociationEventPublisher;
    private ObjectConverter objectConverter;

    @Override
    public PersonAssociation savePersonAssociation(PersonAssociation personAssociation, Authentication authentication)
            throws AcmCreateObjectFailedException
    {
        Long id = personAssociation.getId();
        String personAssociationHistory = null;

        if (id != null)
        {
            PersonAssociation exPersonAssociation = getPersonAssociationDao().find(id);
            AcmMarshaller marshaller = getObjectConverter().getJsonMarshaller();
            // keep copy from the existing object to compare with the updated one
            // otherwise JPA will update all references and no changes can be detected
            personAssociationHistory = marshaller.marshal(exPersonAssociation);
        }

        try
        {
            PersonAssociation savedPersonAssociation = getPersonAssociationTransaction().savePersonAsssociation(personAssociation,
                    authentication);
            getPersonAssociationEventPublisher().publishPersonAssociationEvent(personAssociationHistory, savedPersonAssociation, true);
            return savedPersonAssociation;
        }
        catch (TransactionException e)
        {
            getPersonAssociationEventPublisher().publishPersonAssociationEvent(personAssociationHistory, personAssociation, false);
            throw new AcmCreateObjectFailedException("personAssociation", e.getMessage(), e);
        }
    }

    @Override
    public List<Person> getPersonsInAssociatonsByPersonType(String parentType, Long parentId, String personType) {
        return getPersonAssociationDao().findPersonByParentIdAndParentTypeAndPersonType(parentType, parentId, personType);

    }

    @Override
    public String getPersonAssociations(Long personId, String parentType, int start, int limit, String sort, Authentication auth)
            throws AcmObjectNotFoundException
    {
        return solrJoinDocumentsService.getJoinedDocuments(auth, personId, CHILD_ID_S, PersonOrganizationConstants.PERSON_OBJECT_TYPE,
                CHILD_TYPE_S, PersonOrganizationConstants.PERSON_ASSOCIATION_OBJECT_TYPE, parentType, PARENT_TYPE_S, "parent_object",
                PARENT_REF_S, "id", start, limit, sort);
    }

    @Override
    public PersonAssociation getPersonAssociation(Long id, Authentication auth)
    {
        return personAssociationDao.find(id);
    }

    /**
     * Delete Person association
     *
     * @param id
     *            person association id
     * @param auth
     *            Authentication
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

    public ObjectConverter getObjectConverter()
    {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
    }
}
