package com.armedia.acm.services.search.web.api;

import com.armedia.acm.services.search.model.SearchConstants;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.FacetedSearchService;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by marst on 10/10/16.
 */
@Controller
@RequestMapping({"/api/v1/plugin/search", "/api/latest/plugin/search"})
public class SuggesterAPIController
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private ExecuteSolrQuery executeSolrQuery;

    private FacetedSearchService facetedSearchService;

    @RequestMapping(value = "/suggest", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String suggest(
            @RequestParam(value = "q") String query,
            @RequestParam(value = "core", required = false, defaultValue = "QUICK") String core,
            @RequestParam(value = "filter", required = false, defaultValue = "") String filter,
            Authentication authentication
    ) throws MuleException
    {
        String filterQueries = "";
        filterQueries = Arrays.asList(filter).stream().map(f -> getFacetedSearchService().buildSolrQuery(f)).collect(Collectors.joining("&"));

        switch (core)
        {
            case SearchConstants.CORE_QUICK:
                return getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.QUICK_SUGGESTER_SEARCH, query, 0, 10, "", filterQueries);
            case SearchConstants.CORE_ADVANCED:
                return getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.ADVANCED_SUGGESTER_SEARCH, query, 0, 10, "", filterQueries);
            default:
                return getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.QUICK_SUGGESTER_SEARCH, query, 0, 10, "", filterQueries);
        }
    }

    public ExecuteSolrQuery getExecuteSolrQuery()
    {
        return executeSolrQuery;
    }

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery)
    {
        this.executeSolrQuery = executeSolrQuery;
    }

    public FacetedSearchService getFacetedSearchService()
    {
        return facetedSearchService;
    }

    public void setFacetedSearchService(FacetedSearchService facetedSearchService)
    {
        this.facetedSearchService = facetedSearchService;
    }
}
