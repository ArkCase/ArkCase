package com.armedia.acm.services.search.web.api;

import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.model.TimePeriodForSearch;
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

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by marjan.stefanoski on 21.11.2014.
 */

@Controller
@RequestMapping( { "/api/v1/plugin/search", "/api/latest/plugin/search"} )
public class CaseFilesSearchByDueDateTimeIntervalAPIController {
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private ExecuteSolrQuery executeSolrQuery;

    @RequestMapping(value = "/caseFilesSearch/byTimeInterval", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)

    @ResponseBody
    public String caseFiles(
            @RequestParam(value = "timePeriod" ,required = true) String timePeriod,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "5") int maxRows,
            Authentication authentication,
            HttpServletResponse httpResponse
    ) throws MuleException, UnsupportedEncodingException
    {

        if (log.isDebugEnabled()) {
            if("all".equals(timePeriod)) {
                log.debug("User '" + authentication.getName() + "' is searching for all Case Files grouped by status");
            }
            log.debug("User '" + authentication.getName() + "' is searching for all Case Files grouped by status with dueDate the last:'" + timePeriod + "' ");
        }

        String query = "object_type_s:CASE_FILE";
        String sort = "AND dueDate_tdt ASC";

        switch (TimePeriodForSearch.getTimePeriod(timePeriod)){
             case ALL:
                 break;
             case LAST_MONTH:
                 query += URLEncoder.encode("AND dueDate_tdt:[NOW/DAY-1MONTH TO *]", "UTF-8");
                 break;
             case LAST_YEAR:
                 query += URLEncoder.encode("AND dueDate_tdt:[NOW/DAY-1YEAR TO *]", "UTF-8");
                 break;
             case LAST_WEEK:
                 query += URLEncoder.encode("AND dueDate_tdt:[NOW/DAY-7DAY TO *]", "UTF-8");
                 break;
             default:
                 break;
        }

        query +=" AND group=true AND group.field=status_lcs AND group.limit=0";
        query = query.replaceAll(" ", "+");
        sort = sort.replaceAll(" ", "+");

        String results = getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.ADVANCED_SEARCH,
                query, startRow, maxRows, sort);

        httpResponse.addHeader("X-JSON", results);

        return results;

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
