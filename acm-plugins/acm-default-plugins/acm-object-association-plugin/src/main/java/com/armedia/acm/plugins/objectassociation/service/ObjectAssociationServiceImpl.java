package com.armedia.acm.plugins.objectassociation.service;

/*-
 * #%L
 * ACM Default Plugin: Object Associations
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

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.objectassociation.dao.ObjectAssociationDao;
import com.armedia.acm.plugins.objectassociation.model.AcmChildObjectEntity;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociationConstants;
import com.armedia.acm.services.search.model.solr.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.spring.SpringContextHolder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ObjectAssociationServiceImpl implements ObjectAssociationService
{
    private Logger log = LogManager.getLogger(getClass());
    private SpringContextHolder springContextHolder;

    private ObjectAssociationDao objectAssociationDao;
    private ExecuteSolrQuery executeSolrQuery;
    private ObjectAssociationEventPublisher objectAssociationEventPublisher;

    @Override
    public void addReference(Long id, String number, String type, String title, String status, Long parentId, String parentType)
            throws Exception
    {
        if (id.equals(parentId) && type.equals(parentType))
        {
            throw new Exception("Cannot reference the object itself.");
        }
        if (findByParentTypeAndId(parentType, parentId).stream().filter(o -> (o.getTargetId().equals(id) && o.getTargetType().equals(type)))
                .findAny().isPresent())
        {
            throw new Exception("Selected object is already referenced.");
        }
        AcmAbstractDao<AcmChildObjectEntity> dao = getDaoForChildObjectEntity(parentType);
        if (dao != null)
        {
            AcmChildObjectEntity entity = dao.find(parentId);
            if (entity != null)
            {
                ObjectAssociation oa = makeObjectAssociation(id, number, type, title, status);
                entity.addChildObject(oa);
                dao.save(entity);
            }
        }
    }

    @Override
    @Transactional
    public void delete(Long id)
    {
        objectAssociationDao.delete(id);
    }

    @Override
    public AcmAbstractDao<AcmChildObjectEntity> getDaoForChildObjectEntity(String objectType)
    {
        if (objectType != null)
        {
            Map<String, AcmAbstractDao> daos = getSpringContextHolder().getAllBeansOfType(AcmAbstractDao.class);

            if (daos != null)
            {
                for (AcmAbstractDao<AcmChildObjectEntity> dao : daos.values())
                {
                    if (objectType.equals(dao.getSupportedObjectType()))
                    {
                        return dao;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public ObjectAssociation saveObjectAssociation(ObjectAssociation oa)
    {
        return getObjectAssociationDao().save(oa);
    }

    @Override
    public List<ObjectAssociation> findByParentTypeAndId(String type, Long id)
    {
        return getObjectAssociationDao().findByParentTypeAndId(type, id);
    }

    @Override
    public String getAssociations(Authentication auth, Long parentId, String parentType, String targetType, String orderBy, int start,
                                  int limit) throws AcmObjectNotFoundException
    {
        if (StringUtils.isEmpty(orderBy))
        {
            orderBy = "id asc";
        }

        String associationsQueryString = String.format("object_type_s:REFERENCE AND parent_ref_s:%s", parentId + "-" + parentType);
        if (StringUtils.isNotEmpty(targetType))
        {
            associationsQueryString += String.format(" AND target_type_s:%s", targetType);
        }

        String targetQuery = "{!join from=target_ref_s to=id}";
        targetQuery += associationsQueryString;

        log.debug("object associations query [{}]", associationsQueryString);
        log.debug("target objects query [{}]", targetQuery);
        try
        {
            // Execute all request in parallel to minimize chances for wrong responses
            CompletableFuture<String> targetResponse = executeSolrQuery.getResultsByPredefinedQueryAsync(auth, SolrCore.ADVANCED_SEARCH,
                    targetQuery.toString(), start, limit, orderBy);
            CompletableFuture<String> associationsResponse = executeSolrQuery.getResultsByPredefinedQueryAsync(auth,
                    SolrCore.ADVANCED_SEARCH, associationsQueryString, start, limit, orderBy);
            // wait all completable features to finish
            CompletableFuture.allOf(targetResponse, associationsResponse);

            return combineResults(targetResponse.get(), associationsResponse.get());
        }
        catch (Exception e)
        {
            log.error("Error while executing Solr query: {}", targetQuery, e);
            throw new AcmObjectNotFoundException("ObjectAssociation", null, String.format("Could not execute %s .", targetQuery.toString()),
                    e);
        }
    }

    @Override
    public ObjectAssociation saveAssociation(ObjectAssociation objectAssociation, Authentication auth) throws AcmObjectAssociationException
    {
        String associationState = (objectAssociation.getAssociationId() == null) ? "NEW" : "UPDATE";

        // find all associations to the object originating the association.
        List<ObjectAssociation> existingAssociations = this.findByParentTypeAndId(objectAssociation.getParentType(),
                objectAssociation.getParentId());
        // find if an association exists to the target object of the same type with the one being created
        Optional<ObjectAssociation> duplicate = existingAssociations.stream()
                .filter(oa -> oa.getTargetId().equals(objectAssociation.getTargetId()))
                .filter(oa -> oa.getTargetType().equals(objectAssociation.getTargetType()))
                .filter(oa -> oa.getAssociationType().equals(objectAssociation.getAssociationType())).findAny();

        // if so, it is a duplicate and it is not allowed
        if (duplicate.isPresent())
        {
            log.debug("Assocation of parent object with [{}] id, with target object with [{}] id, of [{}] type already exists.",
                    objectAssociation.getParentId(), objectAssociation.getTargetId(), objectAssociation.getAssociationType());

            throw new AcmObjectAssociationException(
                    String.format("Assocation of parent object with [%s] id, with target object with [%s] id, of [%s] type already exists.",
                            objectAssociation.getParentId(), objectAssociation.getTargetId(), objectAssociation.getAssociationType()));
        }

        ObjectAssociation association = objectAssociationDao.save(objectAssociation);
        if (association != null)
        {
            getObjectAssociationEventPublisher().publishObjectAssociationEvent(association, auth, true, associationState);
        }
        return association;
    }

    @Override
    public void deleteAssociation(Long id, Authentication auth)
    {
        ObjectAssociation objectAssociation = objectAssociationDao.find(id);
        objectAssociationDao.delete(id);
        getObjectAssociationEventPublisher().publishObjectAssociationEvent(objectAssociation, auth, true, "DELETE");
    }

    @Override
    public ObjectAssociation getAssociation(Long id, Authentication auth)
    {
        return objectAssociationDao.find(id);
    }

    protected String combineResults(String targetResult, String associationsResult) throws IOException, AcmObjectNotFoundException
    {
        ObjectMapper om = new ObjectMapper();
        JsonNode targetNode = om.readTree(targetResult);
        JsonNode associationsNode = om.readTree(associationsResult);

        Map<String, JsonNode> targetObjects = new HashMap<>();
        JsonNode associationsDocs = associationsNode.get("response").get("docs");
        JsonNode targetDocs = targetNode.get("response").get("docs");

        for (JsonNode targetObject : targetDocs)
        {
            targetObjects.put(targetObject.get("id").asText(), targetObject);
        }

        for (int i = 0; i < associationsDocs.size(); i++)
        {
            JsonNode associationDoc = associationsDocs.get(i);
            String targetIdString = associationDoc.get("target_id_s").asText() + "-" + associationDoc.get("target_type_s").asText();
            JsonNode targetSolrId = targetObjects.get(targetIdString);
            if (targetSolrId != null)
            {
                // if doesn't have errors, add target object as part of the association
                ((ObjectNode) associationDoc).set("target_object", targetSolrId);
            }
            else
            {
                log.error("Responses doesn't match: associations response = {}, targets response {}", associationsResult, targetResult);
                // TODO handle in another way, instead of creating new empty object
                ((ObjectNode) associationDoc).set("target_object", om.createObjectNode());
            }
        }

        return om.writeValueAsString(associationsNode);
    }

    private ObjectAssociation makeObjectAssociation(Long id, String number, String type, String title, String status)
    {
        ObjectAssociation oa = new ObjectAssociation();
        oa.setTargetId(id);
        oa.setTargetName(number);
        oa.setTargetType(type);
        oa.setTargetTitle(title);
        oa.setStatus(status);
        oa.setAssociationType(ObjectAssociationConstants.REFFERENCE_TYPE);
        return oa;
    }

    public SpringContextHolder getSpringContextHolder()
    {
        return springContextHolder;
    }

    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
    }

    public ObjectAssociationDao getObjectAssociationDao()
    {
        return objectAssociationDao;
    }

    public void setObjectAssociationDao(ObjectAssociationDao objectAssociationDao)
    {
        this.objectAssociationDao = objectAssociationDao;
    }

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery)
    {
        this.executeSolrQuery = executeSolrQuery;
    }

    public ExecuteSolrQuery getExecuteSolrQuery() {
        return executeSolrQuery;
    }

    public ObjectAssociationEventPublisher getObjectAssociationEventPublisher()
    {
        return objectAssociationEventPublisher;
    }

    public void setObjectAssociationEventPublisher(ObjectAssociationEventPublisher objectAssociationEventPublisher)
    {
        this.objectAssociationEventPublisher = objectAssociationEventPublisher;
    }
}
