package com.armedia.acm.services.search.model;

/**
 * Created by armdev on 2/12/15.
 */
public enum SolrCore
{
    QUICK_SEARCH("vm://quickSearchQuery.in"), ADVANCED_SEARCH("vm://advancedSearchQuery.in"), ADVANCED_SUGGESTER_SEARCH(
            "vm://advancedSuggesterQuery.in"), QUICK_SUGGESTER_SEARCH("vm://quickSuggesterQuery.in");

    private String muleEndpointUrl;

    private SolrCore(String muleEndpointUrl)
    {
        this.muleEndpointUrl = muleEndpointUrl;
    }

    public String getMuleEndpointUrl()
    {
        return muleEndpointUrl;
    }
}
