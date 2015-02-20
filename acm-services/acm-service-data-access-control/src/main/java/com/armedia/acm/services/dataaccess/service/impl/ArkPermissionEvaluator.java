package com.armedia.acm.services.dataaccess.service.impl;

import com.armedia.acm.core.AcmObject;
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
 * Created by armdev on 2/12/15.
 */
public class ArkPermissionEvaluator implements PermissionEvaluator
{
    private final transient Logger log = LoggerFactory.getLogger(getClass());

    private ExecuteSolrQuery executeSolrQuery;
    private SearchResults searchResults;

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

        AcmObject domainObject = (AcmObject) targetDomainObject;
        Long objectId = domainObject.getId();
        String objectType = domainObject.getObjectType();

        return checkForReadAccess(authentication, objectId, objectType);
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

        Long id = (Long) targetId;

        if ( log.isTraceEnabled() )
        {
            log.trace("Checking " + permission + " for " + authentication.getName() + " on object of type '" +
                    targetType + "'");
        }

        return checkForReadAccess(authentication, id, targetType);
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
}
