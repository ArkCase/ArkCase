package com.armedia.acm.services.dataaccess.service.impl;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.services.dataaccess.model.DataAccessControlConstants;
import com.armedia.acm.services.participants.dao.AcmParticipantDao;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchResults;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import java.io.Serializable;

/**
 * Determine whether a user is authorized to take an action for a specific object.
 * <p/>
 * For a user to take an action against an object, two conditions have to be met.  They need read access to the
 * object (which proves they are not on the "No Access" list), and they have to be able to take the specific action.  If
 * we only checked the participant privilege table to see if they have the access, they could also be in the
 * No Access list, hence the check to ensure they can read the object.
 * <p/>
 * For read access all we have to do is check Solr.
 * <p/>
 * For other access types, we run queries against the acm_participant_privilege table.  A user has access if:
 * <ul>
 *     <li>The user is specifically in the table and has access</li>
 *     <li>The default user has access</li>
 *     <li>The user is in a group that has access</li>
 * </ul>
 * <p/>
 * The first two conditions are checked in a single query.  The group access is checked in a separate query.
 *
 */
public class ArkPermissionEvaluator implements PermissionEvaluator
{
    private final transient Logger log = LoggerFactory.getLogger(getClass());

    private ExecuteSolrQuery executeSolrQuery;
    private SearchResults searchResults;
    private AcmParticipantDao participantDao;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission)
    {
        if ( targetDomainObject == null )
        {
            log.error("Null targetDomainObject, refusing access!");
            return false;
        }

        if ( log.isTraceEnabled() )
        {
            log.trace("Checking " + permission + " for " + authentication.getName() + " on object of type '" +
                    targetDomainObject.getClass().getName() + "'");
        }

        if ( ! AcmObject.class.isAssignableFrom(targetDomainObject.getClass()) )
        {
            log.info("The type '" + targetDomainObject.getClass().getName() + "' is not an AcmObject; granting access.");
            return true;
        }

        if ( permission == null || !(permission instanceof String) )
        {
            log.error("Permission must be a non-null string... returning false");
            return false;
        }

        AcmObject domainObject = (AcmObject) targetDomainObject;
        Long objectId = domainObject.getId();
        String objectType = domainObject.getObjectType();

        boolean hasRead = checkForReadAccess(authentication, objectId, objectType);

        return evaluateAccess(authentication, objectId, objectType, (String) permission, hasRead);
    }

    private boolean evaluateAccess(Authentication authentication, Long objectId, String objectType, String permission,
                                   boolean hasReadAccess)
    {
        if ( !hasReadAccess )
        {
            // no access since they can't read it
            log.trace("No read access, returning false");
            return hasReadAccess;
        }

        if (DataAccessControlConstants.ACCESS_LEVEL_READ.equals(permission) )
        {
            // they want read, and we already know whether they can read, so we're done
            log.trace("Read access requested - returning read access level");
            return hasReadAccess;
        }

        boolean hasAccessDirectlyOrViaDefaultUser = getParticipantDao().hasObjectAccess(
                authentication.getName(), objectId, objectType, permission, DataAccessControlConstants.ACCESS_GRANT);

        if ( hasAccessDirectlyOrViaDefaultUser )
        {
            // we know they can read it, we're all set.
            log.trace("Has access directly or via default user");
            return hasAccessDirectlyOrViaDefaultUser;
        }

        boolean hasAccessViaGroup = getParticipantDao().hasObjectAccessViaGroup(
                authentication.getName(), objectId, objectType, permission, DataAccessControlConstants.ACCESS_GRANT);

        log.trace("Returning whether they have access via a group - " + hasAccessViaGroup);
        return hasAccessViaGroup;
    }

    private boolean checkForReadAccess(Authentication authentication, Long objectId, String objectType)
    {
        String solrId = objectId + "-" + objectType;

        String query = "id:" + solrId;

        try
        {
            // if the Solr search returns the object, the user has read access to it... eventually we will extend
            // this evaluator to consider additional access levels, but for now we will grant any access so long as
            // the user can read the object.
            String solrResponse = getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.ADVANCED_SEARCH,
                    query, 0, 1, "id asc");

            if ( log.isTraceEnabled() )
            {
                log.trace("Response from SOLR: " + solrResponse);
            }

            int numFound = getSearchResults().getNumFound(solrResponse);

            return numFound > 0;
        }
        catch (MuleException e)
        {
            log.error("Could not check for object access - denying access");
            return false;
        }
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission)
    {
        if ( targetId == null )
        {
            log.error("Null targetId, refusing access!");
            return false;
        }

        if ( ! Long.class.isAssignableFrom(targetId.getClass()) )
        {
            log.error("The id type '" + targetId.getClass().getName() + "' is not a Long - denying access");
            return false;
        }

        if ( permission == null || !(permission instanceof String) )
        {
            log.error("Permission must be a non-null string... returning false");
            return false;
        }

        Long id = (Long) targetId;

        if ( log.isTraceEnabled() )
        {
            log.trace("Checking " + permission + " for " + authentication.getName() + " on object of type '" +
                    targetType + "'");
        }

        boolean hasRead = checkForReadAccess(authentication, id, targetType);

        return evaluateAccess(authentication, id, targetType, (String) permission, hasRead);
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
}
