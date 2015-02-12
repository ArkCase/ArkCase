package com.armedia.acm.services.search.web.api;

import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.SolrSearchService;
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
@RequestMapping( { "/api/v1/plugin/search", "/api/latest/plugin/search"} )
public class AdvancedSearchAPIController {
    private Logger log = LoggerFactory.getLogger(getClass());

    private SolrSearchService solrSearchService;

    @RequestMapping(value = "/advancedSearch", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String advancedSearch(
            @RequestParam(value = "q", required = true) String query,
            @RequestParam(value = "s", required = false, defaultValue = "") String sort,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,
            Authentication authentication
    ) throws MuleException {
        if ( log.isDebugEnabled() ) {
            log.debug("User '" + authentication.getName() + "' is searching for '" + query + "'");
        }

        return getSolrSearchService().search(authentication, SolrCore.ADVANCED_SEARCH, query, startRow, maxRows, sort);
    }

    public SolrSearchService getSolrSearchService()
    {
        return solrSearchService;
    }

    public void setSolrSearchService(SolrSearchService solrSearchService)
    {
        this.solrSearchService = solrSearchService;
    }
}
