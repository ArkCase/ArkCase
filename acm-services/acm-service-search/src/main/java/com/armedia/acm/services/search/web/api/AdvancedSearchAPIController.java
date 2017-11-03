package com.armedia.acm.services.search.web.api;

import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
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

/**
 * Created by marjan.stefanoski on 19.12.2014.
 */

@Controller
@RequestMapping({"/api/v1/plugin/search", "/api/latest/plugin/search"})
public class AdvancedSearchAPIController
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private ExecuteSolrQuery executeSolrQuery;

    @RequestMapping(value = "/advancedSearch", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String advancedSearch(
            @RequestParam(value = "q", required = true) String query,
            @RequestParam(value = "s", required = false, defaultValue = "") String sort,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,
            Authentication authentication
    ) throws MuleException
    {
        if (log.isDebugEnabled())
        {
            log.debug("User '" + authentication.getName() + "' is searching for '" + query + "'");
        }


        // Solr wants the '+' sign in the "facet.range.gap" part of the query to be encoded
        // It is because the '+' shouldn't be interpreted/used as a space but as '+'  on solr side and that's why need to be sent as %2B.
        //
        //
        // Encoding the entire content of the query does not work and it is possible to brake other queries also. That's
        // why we are replacing only the '+' sign in the "facet.range.gap=+" part of the query with "facet.range.gap=%2B".
        //
        // Example UI -> ArkCase (advancedSearch) request that is using facet.range in the query:
        //
        //..advancedSearch?q=object_type_s:COMPLAINT+AND+creator_lcs:ann-acm%26facet.range=create_date_tdt%26facet.range.start=NOW-6MONTHS%26facet.range.end=NOW%26facet.range.gap=%2B1MONTH
        //

        if (query.contains("facet.range.gap=+"))
        {
            query = query.replace("facet.range.gap=+", "facet.range.gap=%2B");
        }

        return getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.ADVANCED_SEARCH, query, startRow, maxRows, sort);
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
