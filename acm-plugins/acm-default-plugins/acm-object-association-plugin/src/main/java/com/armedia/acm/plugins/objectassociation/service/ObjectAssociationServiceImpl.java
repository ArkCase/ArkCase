package com.armedia.acm.plugins.objectassociation.service;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.objectassociation.dao.ObjectAssociationDao;
import com.armedia.acm.plugins.objectassociation.model.AcmChildObjectEntity;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociationConstants;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.spring.SpringContextHolder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


public class ObjectAssociationServiceImpl implements ObjectAssociationService
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private SpringContextHolder springContextHolder;

    private ObjectAssociationDao objectAssociationDao;
    private ExecuteSolrQuery executeSolrQuery;

    @Override
    public void addReference(Long id, String number, String type, String title, String status, Long parentId, String parentType) throws Exception
    {
        if (id.equals(parentId) && type.equals(parentType))
        {
            throw new Exception("Cannot reference the object itself.");
        }
        if (findByParentTypeAndId(parentType, parentId).stream().filter(o -> (o.getTargetId().equals(id) && o.getTargetType().equals(type))).findAny().isPresent())
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
    public String getAssociations(Authentication auth, Long parentId, String parentType, String targetType, String orderBy, int start, int limit) throws AcmObjectNotFoundException
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
            //Execute all request in parallel to minimize chances for wrong responses
            CompletableFuture<String> targetResponse = executeSolrQuery.getResultsByPredefinedQueryAsync(auth, SolrCore.ADVANCED_SEARCH, targetQuery.toString(), start, limit, orderBy);
            CompletableFuture<String> associationsResponse = executeSolrQuery.getResultsByPredefinedQueryAsync(auth, SolrCore.ADVANCED_SEARCH, associationsQueryString, start, limit, orderBy);
            //wait all completable features to finish
            CompletableFuture.allOf(targetResponse, associationsResponse);

            return combineResults(targetResponse.get(), associationsResponse.get());
        } catch (Exception e)
        {
            log.error("Error while executing Solr query: {}", targetQuery, e);
            throw new AcmObjectNotFoundException("ObjectAssociation", null, String.format("Could not execute %s .", targetQuery.toString()), e);
        }
    }

    @Override
    public ObjectAssociation saveAssociation(ObjectAssociation objectAssociation, Authentication auth)
    {
        return objectAssociationDao.save(objectAssociation);
    }

    @Override
    public void deleteAssociation(Long id, Authentication auth)
    {
        objectAssociationDao.delete(id);
    }

    @Override
    public ObjectAssociation getAssociation(Long id, Authentication auth)
    {
        return objectAssociationDao.find(id);
    }

    private String combineResults(String targetResult, String associationsResult) throws IOException
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
            JsonNode targetSolrId = targetObjects.get(associationDoc.get("target_ref_s").asText());
            if (targetSolrId != null)
            {
                //if doesn't have errors, add target object as part of the association
                ((ObjectNode) associationDoc).set("target_object", targetSolrId);
            } else
            {
                log.error("Responses doesn't match: associations response = {}, targets response {}", associationsResult, targetResult);
                //TODO handle in another way, instead of creating new empty object
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
}
