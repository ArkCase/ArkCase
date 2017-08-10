package com.armedia.acm.plugins.person.service;


import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.objectonverter.AcmMarshaller;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.person.dao.PersonAssociationDao;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
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

    private ExecuteSolrQuery executeSolrQuery;

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
        if (StringUtils.isEmpty(sort))
        {
            sort = "id asc";
        }
        StringBuilder parentQuery = new StringBuilder();
        StringBuilder associationsQuery = new StringBuilder();

        String associationsQueryString = String.format("object_type_s:PERSON-ASSOCIATION AND child_id_s:%s AND parent_type_s:%s", personId, parentType);

        parentQuery.append("{!join from=parent_ref_s to=id}");
        parentQuery.append(associationsQueryString);
        associationsQuery.append(associationsQueryString);

        try
        {
            //Execute all request in parallel to minimize chances for wrong responses
            CompletableFuture<String> parentsResponse = executeSolrQuery.getResultsByPredefinedQueryAsync(auth, SolrCore.ADVANCED_SEARCH, parentQuery.toString(), start, limit, sort);
            CompletableFuture<String> associationsResponse = executeSolrQuery.getResultsByPredefinedQueryAsync(auth, SolrCore.ADVANCED_SEARCH, associationsQuery.toString(), start, limit, sort);
            //wait all completable features to finish
            CompletableFuture.allOf(parentsResponse, associationsResponse);

            return combineResults(parentsResponse.get(), associationsResponse.get());
        } catch (Exception e)
        {
            log.error("Error while executing Solr query: {}", parentQuery, e);
            throw new AcmObjectNotFoundException("PersonAssociation", null, String.format("Could not execute %s .", parentQuery.toString()), e);
        }
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

    private String combineResults(String parentResult, String associationsResult) throws IOException, AcmObjectNotFoundException
    {
        ObjectMapper om = new ObjectMapper();
        JsonNode parentNode = om.readTree(parentResult);
        JsonNode associationsNode = om.readTree(associationsResult);

        Map<String, JsonNode> targetObjects = new HashMap<>();
        JsonNode associationsDocs = associationsNode.get("response").get("docs");
        JsonNode targetDocs = parentNode.get("response").get("docs");

        for (JsonNode targetObject : targetDocs)
        {
            targetObjects.put(targetObject.get("id").asText(), targetObject);
        }

        for (int i = 0; i < associationsDocs.size(); i++)
        {
            JsonNode associationDoc = associationsDocs.get(i);
            String parentIdString = associationDoc.get("parent_id_s").asText() + "-" + associationDoc.get("parent_type_s").asText();
            JsonNode parentSolrId = targetObjects.get(parentIdString);
            if (parentSolrId != null)
            {
                //if doesn't have errors, add target object as part of the association
                ((ObjectNode) associationDoc).set("parent_object",
                        parentSolrId);
            } else
            {
                log.error("Responses doesn't match: associations response = {}, targets response {}", associationsResult, parentResult);
                throw new AcmObjectNotFoundException("ObjectAssociation", null, String.format("Could not find document in solr with id %s .", parentIdString), null);
            }
        }
        return om.writeValueAsString(associationsNode);
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

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery)
    {
        this.executeSolrQuery = executeSolrQuery;
    }
}
