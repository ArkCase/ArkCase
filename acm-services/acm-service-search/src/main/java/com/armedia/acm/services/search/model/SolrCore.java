package com.armedia.acm.services.search.model;

/**
 * Created by armdev on 2/12/15.
 */
public enum SolrCore
{
    QUICK_SEARCH("vm://quickSearchQuery.in", "acmQuickSearch"), ADVANCED_SEARCH("vm://advancedSearchQuery.in",
            "acmAdvancedSearch"), ADVANCED_SUGGESTER_SEARCH(
                    "vm://advancedSuggesterQuery.in"), QUICK_SUGGESTER_SEARCH("vm://quickSuggesterQuery.in");

    private String muleEndpointUrl;
    private String core;

    private SolrCore(String muleEndpointUrl)
    {
        this.muleEndpointUrl = muleEndpointUrl;
    }

    private SolrCore(String muleEndpointUrl, String core)
    {
        this.muleEndpointUrl = muleEndpointUrl;
        this.core = core;
    }

    public String getMuleEndpointUrl()
    {
        return muleEndpointUrl;
    }

    public String getCore()
    {
        return core;
    }

}
