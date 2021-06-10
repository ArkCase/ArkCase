package com.armedia.acm.services.dataaccess.service.impl;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.data.service.AcmDataService;
import com.armedia.acm.objectonverter.json.JSONMarshaller;
import com.armedia.acm.services.dataaccess.model.DataAccessControlConfig;
import com.armedia.acm.services.dataaccess.model.DataAccessControlConstants;
import com.armedia.acm.services.dataaccess.service.AccessControlRuleChecker;
import com.armedia.acm.services.participants.dao.AcmParticipantDao;
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.search.exception.SolrException;
import com.armedia.acm.services.search.model.SearchConstants;
import com.armedia.acm.services.search.model.solr.SolrAbstractDocument;
import com.armedia.acm.services.search.model.solr.SolrCore;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchResults;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.spring.SpringContextHolder;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*-
 * #%L
 * ACM Service: Data Access Control
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

/**
 * Determine whether a user is authorized to take an action for a specific object.
 * <p/>
 * For a user to take an action against an object, two conditions have to be met. They need read access to the
 * object (which proves they are not on the "No Access" list), and they have to be able to take the specific action. If
 * we only checked the participant privilege table to see if they have the access, they could also be in the
 * No Access list, hence the check to ensure they can read the object.
 * <p/>
 * For read access all we have to do is check Solr. If the Solr query doesn't return any results, then either
 * the user has no access to the object; or else there is no such object; or perhaps the Solr index hasn't been
 * updated yet (common right after new object creation). Because of the last condition (Solr index not updated
 * yet), we will run a database query to see if the user has access.
 * <p/>
 * For other access types, we run queries against the acm_participant_privilege table. A user has access if:
 * <ul>
 * <li>The user is specifically in the table and has access</li>
 * <li>The default user has access</li>
 * <li>The user is in a group that has access</li>
 * </ul>
 * <p/>
 * The first two conditions are checked in a single query. The group access is checked in a separate query.
 */
public class ArkPermissionEvaluator implements PermissionEvaluator, InitializingBean
{
    private final transient Logger log = LogManager.getLogger(getClass());

    private ExecuteSolrQuery executeSolrQuery;
    private SearchResults searchResults;
    private AcmParticipantDao participantDao;
    private AcmGroupDao groupDao;
    private UserDao userDao;
    private AccessControlRuleChecker accessControlRuleChecker;
    private AcmDataService acmDataService;
    private SpringContextHolder springContextHolder;
    private JSONMarshaller jsonMarshaller;
    private Set<String> assignedObjectTypes;
    private String packagesToScan;
    private DataAccessControlConfig dataAccessControlConfig;

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission)
    {
        if (targetId == null)
        {
            log.error("Null targetId, refusing access!");
            return false;
        }

        if (!Long.class.isAssignableFrom(targetId.getClass()) && !List.class.isAssignableFrom(targetId.getClass()))
        {
            log.error("The id type [{}] is not a List - denying access", targetId.getClass().getName());
            return false;
        }

        if (!(permission instanceof String))
        {
            log.error("Permission must be a non-null string... returning false");
            return false;
        }

        if (!dataAccessControlConfig.getEnableDocumentACL()
                && (targetType == null || targetType.equals("FILE") || targetType.equals("FOLDER")))
        {
            return true;
        }

        // checking access to a single object
        if (Long.class.isAssignableFrom(targetId.getClass()))
        {
            return checkAccessForSingleObject(authentication, (Long) targetId, targetType, permission);
        }

        // checking access to list of objects
        List<Long> ids = (List<Long>) targetId;
        for (Long id : ids)
        {
            // if access is denied for any of the objects in list, then deny access for entire list
            if (!checkAccessForSingleObject(authentication, id, targetType, permission))
            {
                return false;
            }
        }
        return true;
    }

    protected <T> AcmObjectToSolrDocTransformer<T> findTransformerForEntity(Class<T> entityClass)
    {
        @SuppressWarnings("rawtypes")
        Map<String, AcmObjectToSolrDocTransformer> transformers = getSpringContextHolder()
                .getAllBeansOfType(AcmObjectToSolrDocTransformer.class);
        @SuppressWarnings("unchecked")
        AcmObjectToSolrDocTransformer<T> transformer = transformers.values().stream()
                .filter(t -> t.getAcmObjectTypeSupported().equals(entityClass))
                .findFirst()
                .orElse(null);
        return transformer;

    }

    /**
     * Check user access for object with particular id.
     *
     * @param authentication
     *            authentication token
     * @param id
     *            object identifier
     * @param targetType
     *            object type
     * @param permission
     *            requested permission (actionName)
     * @return true if granted, false otherwise
     */
    protected boolean checkAccessForSingleObject(Authentication authentication, Long id, String targetType, Object permission)
    {

        log.trace("Checking [{}] for [{}] on object of type [{}] with id [{}]", permission, authentication.getName(), targetType, id);

        String solrDocument = getSolrDocument(authentication, id, targetType);

        if (solrDocument == null || !checkForReadAccess(solrDocument))
        {
            solrDocument = lookupAndConvertObjectFromDatabase(targetType, id);
            if (solrDocument == null)
            {
                // there really is no such object, or else no DAO or no transformer
                return false;
            }
        }

        // break here and return true if any of AC rules match (see SBI-956)
        if (accessControlRuleChecker.isAccessGranted(authentication, id, targetType, (String) permission, solrDocument))
        {
            return true;
        }

        return evaluateAccess(authentication, id, targetType, Arrays.asList(((String) permission).split("\\|")));
    }

    public String lookupAndConvertObjectFromDatabase(String targetType, Long id)
    {
        log.info("Attempting database lookup for [{}] with id [{}]", targetType, id);
        // here we need to lookup the JPA entity from the database and convert to the expected Solr format,
        // so we can then proceed with the access control checks... since the reason the document could not be
        // found, may be that Solr just hasn't been updated yet.
        AcmAbstractDao<?> dao = getAcmDataService().getDaoByObjectType(targetType);
        if (dao != null)
        {
            log.debug("found DAO of type [{}]", dao.getClass().getName());
            Object jpaEntity = dao.find(id);
            if (jpaEntity != null)
            {
                AcmObjectToSolrDocTransformer transformer = findTransformerForEntity(jpaEntity.getClass());
                if (transformer != null)
                {
                    log.debug("found transformer of type [{}]", transformer.getClass().getName());
                    SolrAbstractDocument solrDoc = transformJpaEntity(jpaEntity, transformer);

                    if (solrDoc != null)
                    {
                        String jsonStr = getJsonMarshaller().marshal(solrDoc);

                        // now we have the json doc, but we have to wrap it in the expected Solr structure
                        jsonStr = "{\"response\":{\"numFound\":1,\"start\":0,\"docs\":[" + jsonStr + "] } }";
                        return jsonStr;
                    }
                }
                else
                {
                    log.warn("No transformer found for entity of type: {}", jpaEntity.getClass().getName());
                }
            }
        }
        else
        {
            // it seems weird that we couldn't even find a dao
            log.warn("No DAO found for object of type {}", targetType);
        }

        return null;
    }

    protected SolrAbstractDocument transformJpaEntity(Object jpaEntity, AcmObjectToSolrDocTransformer transformer)
    {
        // Every transformer implements one of these methods. This code mirrors the Solr
        // lookup in this class, which checks the advanced search core
        SolrAbstractDocument solrDoc = transformer.toSolrAdvancedSearch(jpaEntity);
        if (solrDoc == null)
        {
            solrDoc = transformer.toContentFileIndex(jpaEntity);
            if (solrDoc == null)
            {
                solrDoc = transformer.toSolrAdvancedSearch(jpaEntity);
            }
            if (solrDoc == null)
            {
                // ??? this is really weird.
                log.warn("Transformer of type [{}] did not transform object of type [{}}",
                        transformer.getClass().getName(), jpaEntity.getClass().getName());
            }
        }
        return solrDoc;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission)
    {
        // we do not support permission check on an object instance since the client (sender) can fake it
        throw new UnsupportedOperationException("Checking permissions on an object reference is not supported");
    }

    private boolean evaluateAccess(Authentication authentication, Long objectId, String objectType, List<String> permissions)
    {
        if (permissions.contains(DataAccessControlConstants.ACCESS_LEVEL_READ))
        {
            // they want read, and we already know whether they can read, so we're done
            log.trace("Read access requested - returning read access level");
            return true;
        }

        for (String permission : permissions)
        {
            boolean hasAccessDirectlyOrViaDefaultUser = getParticipantDao().hasObjectAccess(
                    authentication.getName(), objectId, objectType, permission, DataAccessControlConstants.ACCESS_GRANT);

            if (hasAccessDirectlyOrViaDefaultUser)
            {
                // we know they can read it, we're all set.
                log.trace("Has access directly or via default user");
                return hasAccessDirectlyOrViaDefaultUser;
            }

            boolean hasAccessViaGroup = hasObjectAccessViaGroup(authentication.getName(), objectId, objectType,
                    permission, DataAccessControlConstants.ACCESS_GRANT);

            if (hasAccessViaGroup)
            {
                log.trace("Has access via a group");
                return hasAccessViaGroup;
            }

        }

        log.trace("User has no access to object");
        return false;
    }

    private boolean hasObjectAccessViaGroup(String principal, Long objectId, String objectType,
            String objectAction, String access)
    {
        AcmUser user = getUserDao().findByUserId(principal);
        List<AcmGroup> userGroups = getGroupDao().findByUserMember(user);

        Stream<String> principalDirectAuthorities = userGroups.stream()
                .map(AcmGroup::getName);

        Stream<String> principalAuthoritiesPerAscendants = userGroups.stream()
                .flatMap(AcmGroup::getAscendantsStream);

        Set<String> principalAllAuthorities = Stream.concat(principalAuthoritiesPerAscendants, principalDirectAuthorities)
                .collect(Collectors.toSet());

        return getParticipantDao().hasObjectAccessViaGroup(principalAllAuthorities, objectId, objectType,
                objectAction, access);
    }

    private String getSolrDocument(Authentication authentication, Long objectId, String objectType)
    {
        String solrId = objectId + "-" + objectType;

        String query = "id:" + solrId;

        try
        {
            boolean shouldIncludeACLFilter = isAssignedObjectType(objectType);
            boolean filterSubscriptionEvents = false;
            boolean indent = true;

            // if the Solr search returns the object, the user has read access to it.
            String result = getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.ADVANCED_SEARCH, query,
                    0, 1, "id asc", indent, objectType,
                    filterSubscriptionEvents, SearchConstants.DEFAULT_FIELD, shouldIncludeACLFilter,
                    dataAccessControlConfig.getIncludeDenyAccessFilter(), dataAccessControlConfig.getEnableDocumentACL());

            if (result.contains("numFound\":0"))
            {
                result = getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.ADVANCED_SEARCH, query, 0,
                        1, "id asc", indent, objectType, filterSubscriptionEvents,
                        SearchConstants.DEFAULT_FIELD, shouldIncludeACLFilter,
                        dataAccessControlConfig.getIncludeDenyAccessFilter(), dataAccessControlConfig.getEnableDocumentACL());
            }
            return result;
        }
        catch (SolrException e)
        {
            log.error("Unable to retrieve Solr document for object with id [{}] of type [{}]", objectId, objectType, e);
            return null;
        }
    }

    private boolean checkForReadAccess(String solrResponse)
    {
        int numFound = getSearchResults().getNumFound(solrResponse);
        return numFound > 0;
    }

    protected boolean isAssignedObjectType(String objectType)
    {
        return assignedObjectTypes.contains(objectType);
    }

    public ExecuteSolrQuery getExecuteSolrQuery()
    {
        return executeSolrQuery;
    }

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery)
    {
        this.executeSolrQuery = executeSolrQuery;
    }

    public SearchResults getSearchResults()
    {
        return searchResults;
    }

    public void setSearchResults(SearchResults searchResults)
    {
        this.searchResults = searchResults;
    }

    public AcmParticipantDao getParticipantDao()
    {
        return participantDao;
    }

    public void setParticipantDao(AcmParticipantDao participantDao)
    {
        this.participantDao = participantDao;
    }

    public AcmGroupDao getGroupDao()
    {
        return groupDao;
    }

    public void setGroupDao(AcmGroupDao groupDao)
    {
        this.groupDao = groupDao;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public AccessControlRuleChecker getAccessControlRuleChecker()
    {
        return accessControlRuleChecker;
    }

    public void setAccessControlRuleChecker(AccessControlRuleChecker accessControlRuleChecker)
    {
        this.accessControlRuleChecker = accessControlRuleChecker;
    }

    public boolean isEnableDocumentACL()
    {
        return dataAccessControlConfig.getEnableDocumentACL();
    }

    public AcmDataService getAcmDataService()
    {
        return acmDataService;
    }

    public void setAcmDataService(AcmDataService acmDataService)
    {
        this.acmDataService = acmDataService;
    }

    public SpringContextHolder getSpringContextHolder()
    {
        return springContextHolder;
    }

    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
    }

    public JSONMarshaller getJsonMarshaller()
    {
        return jsonMarshaller;
    }

    public void setJsonMarshaller(JSONMarshaller jsonMarshaller)
    {
        this.jsonMarshaller = jsonMarshaller;
    }

    public Set<String> getAssignedObjectTypes()
    {
        return assignedObjectTypes;
    }

    public String getPackagesToScan()
    {
        return packagesToScan;
    }

    public void setPackagesToScan(String packagesToScan)
    {
        this.packagesToScan = packagesToScan;
    }

    public DataAccessControlConfig getDataAccessControlConfig()
    {
        return dataAccessControlConfig;
    }

    public void setDataAccessControlConfig(DataAccessControlConfig dataAccessControlConfig)
    {
        this.dataAccessControlConfig = dataAccessControlConfig;
    }

    @Override
    public void afterPropertiesSet()
    {
        Object[] packages = Arrays.stream(packagesToScan.split(","))
                .map(it -> StringUtils.substringBeforeLast(it, ".*"))
                .toArray();

        Reflections reflections = new Reflections(packages);

        Set<Class<? extends AcmAssignedObject>> acmObjects = reflections.getSubTypesOf(AcmAssignedObject.class);

        assignedObjectTypes = acmObjects.stream()
                .peek(it -> log.debug("Found assigned object [{}]", it.getSimpleName()))
                .map(it -> {
                    try
                    {
                        return it.newInstance().getObjectType();
                    }
                    catch (InstantiationException | IllegalAccessException e)
                    {
                        log.warn("Can not determine object type for class [{}]", it.getSimpleName());
                    }
                    return null;
                }).filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
