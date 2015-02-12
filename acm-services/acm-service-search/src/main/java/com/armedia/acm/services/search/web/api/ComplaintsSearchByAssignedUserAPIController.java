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

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;

/**
 * Created by marjan.stefanoski on 20.11.2014.
 */
@Controller
@RequestMapping( { "/api/v1/plugin/search", "/api/latest/plugin/search"} )
public class ComplaintsSearchByAssignedUserAPIController {

    private Logger log = LoggerFactory.getLogger(getClass());

    private SolrSearchService solrSearchService;

    @RequestMapping(value = "/complaintsSearch", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String complaints(
             @RequestParam(value = "user", required = true) String userId,
             @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
             @RequestParam(value = "n", required = false, defaultValue = "5") int maxRows,
             Authentication authentication,
             HttpServletResponse httpResponse
    ) throws MuleException {

        if ( log.isDebugEnabled() ) {
            log.debug("User '" + authentication.getName() + "' is searching for Complaints assigned to:'" + userId + "' ");
        }

        String query = "object_type_s:COMPLAINT AND assignee_id_lcs:" + URLEncoder.encode(userId) + " AND -status_lcs:CLOSE";
        String sort = "dueDate_tdt ASC";

        query = query.replaceAll(" ", "+");
        sort = sort.replaceAll(" ", "+");

        String results = getSolrSearchService().search(authentication, SolrCore.ADVANCED_SEARCH, query, startRow, maxRows, sort);

        httpResponse.addHeader("X-JSON", results);

        return results;
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
