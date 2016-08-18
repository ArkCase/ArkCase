package com.armedia.acm.plugins.casefile.web.api;

import com.armedia.acm.services.search.model.SearchConstants;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchResults;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by riste.tutureski on 9/22/2015.
 */
@Controller
@RequestMapping({"/api/v1/plugin/casefile/number/by/queue", "/api/latest/plugin/casefile/number/by/queue"})
public class GetNumberOfActiveCaseFilesByQueueAPIController
{
    private Logger LOG = LoggerFactory.getLogger(getClass());

    private ExecuteSolrQuery executeSolrQuery;
    private SearchResults searchResults;

    /**
     * REST api for retrieving active case files by queue
     *
     * @param authentication
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public Map<String, Long> getNumberOfActiveCaseFilesByQueue(Authentication authentication)
    {
        LOG.debug("Get number of active Case Files by queue.");

        setSearchResults(new SearchResults());

        List<Object> queues = getQueues(authentication);
        List<Object> facet = getFacet(authentication);

        return getNumberOfActiveCaseFilesByQueue(queues, facet);
    }

    /**
     * This method will return Solr response as String for queues
     *
     * @param authentication - authentication object
     * @param start          - start index for the page
     * @param n              - number of elements in the page
     * @return - Solr response in string representation
     */
    private String getSolrQueuesResponse(Authentication authentication, int start, int n)
    {
        String solrResponse = null;
        String query = "object_type_s:QUEUE&sort=queue_order_s ASC";

        try
        {
            solrResponse = getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.QUICK_SEARCH, query, start, n, "");
        } catch (MuleException e)
        {
            LOG.error("Error while executing Solr query: {}", query, e);
        }

        return solrResponse;
    }

    /**
     * This method will return Solr response as String for facet search
     *
     * @param authentication - authentication object
     * @return - Solr response in string representation
     */
    private String getSolrFacetResponse(Authentication authentication)
    {
        String solrResponse = null;
        String facetQuery = "object_type_s:CASE_FILE AND " + SearchConstants.PROPERTY_QUEUE_NAME_S + ":*&rows=1&fl=id&wt=json&indent=true&facet=true&facet.field=" + SearchConstants.PROPERTY_QUEUE_NAME_S;

        try
        {
            solrResponse = getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.QUICK_SEARCH, facetQuery, 0, 1, "");
        } catch (MuleException e)
        {
            LOG.error("Error while executing Solr query: {}", facetQuery, e);
        }

        return solrResponse;
    }

    /**
     * Generate a map with queue names (as keys) and case files count (as values)
     *
     * @param queuesValues - queue names in the list
     * @param facetValues  - facet results in the list
     * @return - map with queue names and case files count
     */
    private Map<String, Long> getNumberOfActiveCaseFilesByQueue(List<Object> queuesValues, List<Object> facetValues)
    {
        Map<String, Long> retval = queuesValues.stream().collect(Collectors.toMap(queueName -> (String) queueName, queueName -> findValue((String) queueName, facetValues), (v1, v2) -> v2, LinkedHashMap::new));

        return retval;
    }

    /**
     * This method will return queue names in the list. The result is taken from the Solr
     *
     * @param authentication
     * @return
     */
    private List<Object> getQueues(Authentication authentication)
    {
        final List<Object> queuesValues = new ArrayList<>();

        int start = 0;
        int n = 50;
        boolean skipLoop = false;
        do
        {
            // Take response from solr for requested page
            String solrResponse = getSolrQueuesResponse(authentication, start, n);
            if (solrResponse != null)
            {
                // Get documents found
                JSONArray docs = getSearchResults().getDocuments(solrResponse);
                if (docs != null && docs.length() > 0)
                {
                    start += n;
                    queuesValues.addAll(getSearchResults().getListForField(docs, SearchConstants.PROPERTY_QUEUE_NAME_S));
                } else
                {
                    // Skip looping if there is no more results
                    skipLoop = true;
                }
            } else
            {
                // Skip looping if there is no any response from the Solr
                skipLoop = true;
            }
        } while (!skipLoop);

        return queuesValues;
    }

    /**
     * This method will return facet results in the list. The result is taken from the Solr
     *
     * @param authentication - authentication object
     * @return - list of facet results
     */
    private List<Object> getFacet(Authentication authentication)
    {
        final List<Object> facetValues = new ArrayList<>();

        // Take response from solr
        String solrFacetResponse = getSolrFacetResponse(authentication);
        if (solrFacetResponse != null)
        {
            // Get facet search values
            JSONObject facetFields = getSearchResults().getFacetFields(solrFacetResponse);
            if (facetFields != null)
            {
                // Add all results in the list
                facetValues.addAll(getSearchResults().extractObjectList(facetFields, SearchConstants.PROPERTY_QUEUE_NAME_S));
            }
        }

        return facetValues;
    }

    /**
     * This method will try to get value for given queue name. The search is made in the facet results list
     *
     * @param queueName       - queue name for which we will try to find any value in the facet results list
     * @param facetFieldValue - facet results list
     * @return - number of case files in the queue
     */
    private Long findValue(String queueName, List<Object> facetFieldValue)
    {
        if (queueName != null && facetFieldValue != null)
        {
            // Take the index for queue name
            int index = facetFieldValue.indexOf(queueName);
            if (index > -1)
            {
                try
                {
                    // Try to find the next element in the list. It should represent number of case files in the queue
                    Integer count = (Integer) facetFieldValue.get(index + 1);
                    return new Long(count);
                } catch (Exception e)
                {
                    LOG.warn("Cannot create Long value. 0 wil be used instead.");
                }
            }
        }

        return new Long(0);
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
