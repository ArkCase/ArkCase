package com.armedia.acm.services.search.web.api;

import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.model.TimePeriodForSearch;
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

/**
 * Created by marjan.stefanoski on 24.11.2014.
 */
@Controller
@RequestMapping( { "/api/v1/plugin/search", "/api/latest/plugin/search"} )
public class ComplaintsSearchByCreatedDateAPIController {

        private transient final Logger log = LoggerFactory.getLogger(getClass());

        private SolrSearchService solrSearchService;

        @RequestMapping(value = "/complaintsSearch/byTimeInterval", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)

        @ResponseBody
        public String complaints(
                @RequestParam(value = "timePeriod", required = false, defaultValue = "all") String timePeriod,
                @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
                @RequestParam(value = "n", required = false, defaultValue = "5") int maxRows,
                Authentication authentication,
                HttpServletResponse httpResponse
        ) throws MuleException {

            if (log.isDebugEnabled()) {
                if("all".equals(timePeriod)) {
                    log.debug("User '" + authentication.getName() + "' is searching for all Case Files grouped by status");
                }
                log.debug("User '" + authentication.getName() + "' is searching for all Case Files grouped by status with dueDate the last:'" + timePeriod + "' ");
            }

            String query = "object_type_s:COMPLAINT";// AND creator_lcs:" + URLEncoder.encode(userId);
            String sort = "create_date_tdt ASC";

            switch (TimePeriodForSearch.getTimePeriod(timePeriod)){
                case ALL:
                    break;
                case LAST_MONTH:
                    query += "AND create_date_tdt:[NOW/DAY-1MONTH TO *]";
                    break;
                case LAST_YEAR:
                    query += "AND create_date_tdt:[NOW/DAY-1YEAR TO *]";
                    break;
                case LAST_WEEK:
                    query += "AND create_date_tdt:[NOW/DAY-7DAY TO *]";
                    break;
                default:
                    break;
            }


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


