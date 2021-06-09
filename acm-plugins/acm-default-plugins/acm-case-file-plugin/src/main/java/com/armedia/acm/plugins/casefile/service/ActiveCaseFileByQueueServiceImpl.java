package com.armedia.acm.plugins.casefile.service;

/*-
 * #%L
 * ACM Default Plugin: Case File
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import com.armedia.acm.services.search.exception.SolrException;
import com.armedia.acm.services.search.model.SearchConstants;
import com.armedia.acm.services.search.model.solr.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchResults;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author aleksandar.bujaroski
 */
public class ActiveCaseFileByQueueServiceImpl implements ActiveCaseFileByQueueService
{

    private Logger LOG = LogManager.getLogger(getClass());

    private ExecuteSolrQuery executeSolrQuery;

    /**
     * This method will return Solr response as String for queues
     *
     * @param authentication
     *            - authentication object
     * @param start
     *            - start index for the page
     * @param n
     *            - number of elements in the page
     * @return - Solr response in string representation
     */
    @Override
    public String getSolrQueuesResponse(Authentication authentication, int start, int n)
    {
        String solrResponse = null;
        String query = "object_type_s:QUEUE";
        String sortParam = SearchConstants.PROPERTY_QUEUE_ORDER + " ASC";

        try
        {
            solrResponse = getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.ADVANCED_SEARCH, query, start, n,
                    sortParam);
        }
        catch (SolrException e)
        {
            LOG.error("Error while executing Solr query: {}", query, e);
        }

        return solrResponse;
    }

    /**
     * This method will return Solr response as String for facet search
     *
     * @param authentication
     *            - authentication object
     * @return - Solr response in string representation
     */
    @Override
    public String getSolrFacetResponse(Authentication authentication)
    {
        String solrResponse = null;
        String facetQuery = "object_type_s:CASE_FILE AND " + SearchConstants.PROPERTY_QUEUE_NAME_S
                + ":*&rows=1&fl=id&wt=json&indent=true&facet=true&facet.field=" + SearchConstants.PROPERTY_QUEUE_NAME_S;

        try
        {
            solrResponse = getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.ADVANCED_SEARCH, facetQuery, 0, 1, "");
        }
        catch (SolrException e)
        {
            LOG.error("Error while executing Solr query: {}", facetQuery, e);
        }

        return solrResponse;
    }

    /**
     * Generate a map with queue names (as keys) and case files count (as values)
     *
     * @param queuesValues
     *            - queue names in the list
     * @param facetValues
     *            - facet results in the list
     * @return - map with queue names and case files count
     */
    @Override
    public Map<String, Long> getNumberOfActiveCaseFilesByQueue(List<Object> queuesValues, List<Object> facetValues)
    {
        Map<String, Long> retval = queuesValues.stream().collect(Collectors.toMap(queueName -> (String) queueName,
                queueName -> findValue((String) queueName, facetValues), (v1, v2) -> v2, LinkedHashMap::new));

        return retval;
    }

    /**
     * This method will return queue names in the list. The result is taken from the Solr
     *
     * @param authentication
     * @return
     */
    @Override
    public List<Object> getQueues(Authentication authentication, SearchResults searchResults)
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
                JSONArray docs = searchResults.getDocuments(solrResponse);
                if (docs != null && docs.length() > 0)
                {
                    start += n;
                    queuesValues.addAll(searchResults.getListForField(docs, SearchConstants.PROPERTY_QUEUE_NAME_S));
                }
                else
                {
                    // Skip looping if there is no more results
                    skipLoop = true;
                }
            }
            else
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
     * @param authentication
     *            - authentication object
     * @return - list of facet results
     */
    @Override
    public List<Object> getFacet(Authentication authentication, SearchResults searchResults)
    {
        final List<Object> facetValues = new ArrayList<>();

        // Take response from solr
        String solrFacetResponse = getSolrFacetResponse(authentication);
        if (solrFacetResponse != null)
        {
            // Get facet search values
            JSONObject facetFields = searchResults.getFacetFields(solrFacetResponse);
            if (facetFields != null)
            {
                // Add all results in the list
                facetValues.addAll(searchResults.extractObjectList(facetFields, SearchConstants.PROPERTY_QUEUE_NAME_S));
            }
        }

        return facetValues;
    }

    /**
     * This method will try to get value for given queue name. The search is made in the facet results list
     *
     * @param queueName
     *            - queue name for which we will try to find any value in the facet results list
     * @param facetFieldValue
     *            - facet results list
     * @return - number of case files in the queue
     */
    @Override
    public Long findValue(String queueName, List<Object> facetFieldValue)
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
                }
                catch (Exception e)
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
}
