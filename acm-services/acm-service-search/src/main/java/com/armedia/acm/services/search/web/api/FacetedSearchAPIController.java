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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by marjan.stefanoski on 17.12.2014.
 */
@Controller
@RequestMapping({"/api/v1/plugin/search", "/api/latest/plugin/search"})
public class FacetedSearchAPIController
{

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private ExecuteSolrQuery executeSolrQuery;
    private FacetedSearchService facetedSearchService;


    @RequestMapping(value = "/facetedSearch", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String mainNotFilteredFacetedSerach(
            @RequestParam(value = "q", required = true) String q,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "500") int maxRows,
            @RequestParam(value = "filters", required = false, defaultValue = "") String[] filters,
            @RequestParam(value = "s", required = false, defaultValue = "create_date_tdt DESC") String sortSpec,
            Authentication authentication
    ) throws MuleException, UnsupportedEncodingException
    {
        log.debug("User '" + authentication.getName() + "' is performing facet search for the query: '" + q + "' ");

        String facetKeys = getFacetedSearchService().getFacetKeys();

        String filterQueries = "";
        if (filters != null)
        {
            filterQueries = Arrays.asList(filters).stream().map(f -> getFacetedSearchService().buildSolrQuery(f)).collect(Collectors.joining("&"));
        }

        String rowQueryParameters = facetKeys + filterQueries;
        String sort = sortSpec == null ? "" : sortSpec.trim();

        // if the query ends in a *, it has to be quoted, or Solr will not find anything somehow.
        if (q.endsWith("*"))
        {
            q = "\"" + q + "\"";
        }

        String query = SearchConstants.CATCH_ALL_QUERY + q;

        query = getFacetedSearchService().updateQueryWithExcludedObjects(query, rowQueryParameters);
        query += getFacetedSearchService().buildHiddenDocumentsFilter();
        query = URLEncoder.encode(query, SearchConstants.FACETED_SEARCH_ENCODING);

        String results = getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.ADVANCED_SEARCH,
                query, startRow, maxRows, sort, rowQueryParameters);
        String res = getFacetedSearchService().replaceEventTypeName(results);
        return res;

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
