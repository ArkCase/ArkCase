package com.armedia.acm.services.search.model.solr;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Created by armdev on 10/22/14.
 */
public interface AcmObjectToSolrDocTransformer<T>
{
    SolrAdvancedSearchDocument toSolrAdvancedSearch(T in) throws JsonProcessingException;

    SolrDocument toSolrQuickSearch(T in) throws JsonProcessingException;

    boolean isAcmObjectTypeSupported(Class acmObjectType);
}
