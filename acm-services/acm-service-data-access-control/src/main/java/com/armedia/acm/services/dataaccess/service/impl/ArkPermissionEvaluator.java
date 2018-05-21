package com.armedia.acm.services.dataaccess.service.impl;

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

import com.armedia.acm.services.dataaccess.model.DataAccessControlConstants;
import com.armedia.acm.services.dataaccess.service.AccessControlRuleChecker;
import com.armedia.acm.services.participants.dao.AcmParticipantDao;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchResults;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;

import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Determine whether a user is authorized to take an action for a specific object.
 * <p/>
 * For a user to take an action against an object, two conditions have to be met. They need read access to the
 * object (which proves they are not on the "No Access" list), and they have to be able to take the specific action. If
 * we only checked the participant privilege table to see if they have the access, they could also be in the
 * No Access list, hence the check to ensure they can read the object.
 * <p/>
 * For read access all we have to do is check Solr.
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
public class ArkPermissionEvaluator implements PermissionEvaluator
{
    private final transient Logger log = LoggerFactory.getLogger(getClass());

    private ExecuteSolrQuery executeSolrQuery;
    private SearchResults searchResults;
    private AcmParticipantDao participantDao;
    private AcmGroupDao groupDao;
    private UserDao userDao;
    private AccessControlRuleChecker accessControlRuleChecker;
    private boolean enableDocumentACL;

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
            log.error("The id type '" + targetId.getClass().getName() + "' is not a List - denying access");
            return false;
        }

        if (permission == null || !(permission instanceof String))
        {
            log.error("Permission must be a non-null string... returning false");
            return false;
        }

        if (!isEnableDocumentACL() && (targetType == null || targetType.equals("FILE") || targetType.equals("FOLDER")))
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
    private boolean checkAccessForSingleObject(Authentication authentication, Long id, String targetType, Object permission)
    {
        if (log.isTraceEnabled())
        {
            log.trace("Checking " + permission + " for " + authentication.getName() + " on object of type '" +
                    targetType + "'");
        }

        String solrDocument = getSolrDocument(authentication, id, targetType);

        if (solrDocument == null || !checkForReadAccess(solrDocument))
        {
            // no access since they can't read it
            log.warn("No read access, returning false");
            return false;
        }

        // break here and return true if any of AC rules match (see SBI-956)
        if (accessControlRuleChecker.isAccessGranted(authentication, id, targetType, (String) permission, solrDocument))
        {
            return true;
        }

        return evaluateAccess(authentication, id, targetType, Arrays.asList(((String) permission).split("\\|")));
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
            // if the Solr search returns the object, the user has read access to it... eventually we will extend
            // this evaluator to consider additional access levels, but for now we will grant any access so long as
            // the user can read the object.
            String result = getExecuteSolrQuery()
                    .getResultsByPredefinedQuery(authentication, SolrCore.ADVANCED_SEARCH, query, 0, 1, "id asc", objectType);
            if (result.contains("numFound\":0"))
                result = getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.QUICK_SEARCH, query, 0, 1, "id asc",
                        objectType);
            return result;
        }
        catch (MuleException e)
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
        return enableDocumentACL;
    }

    public void setEnableDocumentACL(boolean enableDocumentACL)
    {
        this.enableDocumentACL = enableDocumentACL;
    }
}
