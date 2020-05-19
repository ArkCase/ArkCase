package com.armedia.acm.plugins.consultation.service;

import com.armedia.acm.services.search.service.SearchResults;

import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;

/**
 * @author aleksandar.bujaroski
 */
public interface ActiveConsultationByQueueService
{
    String getSolrQueuesResponse(Authentication authentication, int start, int n);

    String getSolrFacetResponse(Authentication authentication);

    Map<String, Long> getNumberOfActiveConsultationsByQueue(List<Object> queuesValues, List<Object> facetValues);

    List<Object> getQueues(Authentication authentication, SearchResults searchResults);

    List<Object> getFacet(Authentication authentication, SearchResults searchResults);

    Long findValue(String queueName, List<Object> facetFieldValue);
}
