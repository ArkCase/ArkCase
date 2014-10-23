package com.armedia.acm.services.search.model.solr;

/**
 * Created by armdev on 10/22/14.
 */
public interface AcmObjectToSolrDocTransformer<T extends Object>
{
    SolrAdvancedSearchDocument toSolrAdvancedSearch(T in);

    SolrDocument toSolrQuickSearch(T in);

    boolean isAcmObjectTypeSupported(Class acmObjectType);
}
