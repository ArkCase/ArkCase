package com.armedia.acm.services.search.service;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.services.search.model.SolrCore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class SolrJoinDocumentsServiceImpl implements SolrJoinDocumentsService
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private ExecuteSolrQuery executeSolrQuery;

    @Override
    public String getJoinedDocuments(Authentication auth, Long ownerId, String ownerIdFieldName,
                                     String ownerType, String ownerTypeFieldName,
                                     String referenceType,
                                     String targetType, String targetTypeFieldName,
                                     String storeTargetObjectFieldName,
                                     String joinFromField, String joinToField, int start, int limit, String sort) throws AcmObjectNotFoundException
    {

        Objects.requireNonNull(ownerId, "ownerId is required");
        Objects.requireNonNull(ownerIdFieldName, "ownerIdFieldName is required");
        Objects.requireNonNull(ownerType, "ownerType is required");
        Objects.requireNonNull(ownerTypeFieldName, "ownerTypeFieldName is required");
        Objects.requireNonNull(referenceType, "referenceType is required");
        Objects.requireNonNull(targetType, "targetType is required");
        Objects.requireNonNull(targetTypeFieldName, "targetTypeFieldName is required");
        Objects.requireNonNull(storeTargetObjectFieldName, "storeTargetObjectFieldName is required");
        Objects.requireNonNull(joinFromField, "joinFromField is required");
        Objects.requireNonNull(joinToField, "joinToField is required");

        sort = StringUtils.isEmpty(sort) ? "id asc" : sort;

        String associationsQuery = "object_type_s" + ":" + referenceType
                + " AND " + ownerIdFieldName + ":" + ownerId
                + " AND " + ownerTypeFieldName + ":" + ownerType
                + " AND " + targetTypeFieldName + ":" + targetType;
        log.debug("association query string [{}]", associationsQuery.toString());


        String targetQuery = "{!join from=" + joinFromField + " to=" + joinToField + "}";
        targetQuery += associationsQuery;
        log.debug("association join query string [{}]", targetQuery.toString());

        try
        {
            //Execute all request in parallel to minimize chances for wrong responses
            CompletableFuture<String> targetResponse = executeSolrQuery.getResultsByPredefinedQueryAsync(auth, SolrCore.ADVANCED_SEARCH, targetQuery.toString(), start, limit, sort);
            CompletableFuture<String> associationsResponse = executeSolrQuery.getResultsByPredefinedQueryAsync(auth, SolrCore.ADVANCED_SEARCH, associationsQuery.toString(), start, limit, sort);
            //wait all completable features to finish
            CompletableFuture.allOf(targetResponse, associationsResponse);

            return combineResults(targetResponse.get(), associationsResponse.get(), storeTargetObjectFieldName, joinFromField, joinToField);
        } catch (Exception e)
        {
            log.error("Error while executing Solr query: {}", targetQuery, e);
            throw new AcmObjectNotFoundException(referenceType, null, String.format("Could not execute %s .", targetQuery.toString()), e);
        }
    }

    private String combineResults(String parentResult, String associationsResult, String storeTargetObjectFieldName, String joinFromField, String joinToField) throws IOException, AcmObjectNotFoundException
    {
        ObjectMapper om = new ObjectMapper();
        JsonNode parentNode = om.readTree(parentResult);
        JsonNode associationsNode = om.readTree(associationsResult);

        Map<String, JsonNode> parentObjects = new HashMap<>();
        JsonNode associationsDocs = associationsNode.get("response").get("docs");
        JsonNode targetDocs = parentNode.get("response").get("docs");

        for (JsonNode parentObject : targetDocs)
        {
            parentObjects.put(parentObject.get(joinToField).asText(), parentObject);
        }

        for (int i = 0; i < associationsDocs.size(); i++)
        {
            JsonNode associationDoc = associationsDocs.get(i);
            String parentIdString = associationDoc.get(joinFromField).asText();
            JsonNode parentObject = parentObjects.get(parentIdString);
            if (parentObject != null)
            {
                //if doesn't have errors, add parent object as part of the association
                ((ObjectNode) associationDoc).set(storeTargetObjectFieldName,
                        parentObject);
            } else
            {
                log.error("Responses doesn't match: associations response = {}, targets response {}", associationsResult, parentResult);
                throw new AcmObjectNotFoundException("ObjectAssociation", null, String.format("Could not find document in solr with id %s .", parentIdString), null);
            }
        }
        return om.writeValueAsString(associationsNode);
    }

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery)
    {
        this.executeSolrQuery = executeSolrQuery;
    }
}
