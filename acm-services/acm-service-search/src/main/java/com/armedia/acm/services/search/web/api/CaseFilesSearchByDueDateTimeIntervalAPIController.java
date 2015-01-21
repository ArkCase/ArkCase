package com.armedia.acm.services.search.web.api;

import com.armedia.acm.services.search.model.TimePeriodForSearch;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Created by marjan.stefanoski on 21.11.2014.
 */

@Controller
@RequestMapping( { "/api/v1/plugin/search", "/api/latest/plugin/search"} )
public class CaseFilesSearchByDueDateTimeIntervalAPIController {
    private Logger log = LoggerFactory.getLogger(getClass());

    private MuleClient muleClient;

    @RequestMapping(value = "/caseFilesSearch/byTimeInterval", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)

    @ResponseBody
    public String caseFiles(
            @RequestParam(value = "timePeriod" ,required = true) String timePeriod,
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

        String query = "object_type_s:CASE_FILE";// AND creator_lcs:" + URLEncoder.encode(userId);
        String sort = "AND dueDate_tdt ASC";

        switch (TimePeriodForSearch.getTimePeriod(timePeriod)){
             case ALL:
                 break;
             case LAST_MONTH:
                 query += URLEncoder.encode("AND dueDate_tdt:[NOW/DAY-1MONTH TO *]");
                 break;
             case LAST_YEAR:
                 query += URLEncoder.encode("AND dueDate_tdt:[NOW/DAY-1YEAR TO *]");
                 break;
             case LAST_WEEK:
                 query += URLEncoder.encode("AND dueDate_tdt:[NOW/DAY-7DAY TO *]");
                 break;
             default:
                 break;
        }

        query +=" AND group=true AND group.field=status_lcs AND group.limit=0";
        query = query.replaceAll(" ", "+");
        sort = sort.replaceAll(" ", "+");

        Map<String, Object> headers = new HashMap<>();
        headers.put("query", query);
        headers.put("firstRow", startRow);
        headers.put("maxRows", maxRows);
        headers.put("sort", sort);
        headers.put("acmUser", authentication);

        MuleMessage response = getMuleClient().send("vm://advancedSearchQuery.in", "", headers);

        log.debug("Response type: " + response.getPayload().getClass());

        if ( response.getPayload() instanceof String ) {
            httpResponse.addHeader("X-JSON", response.getPayload().toString());
            return (String) response.getPayload();
        }

        throw new IllegalStateException("Unexpected payload type: " + response.getPayload().getClass().getName());

    }

    public MuleClient getMuleClient() {
        return muleClient;
    }

    public void setMuleClient(MuleClient muleClient) {
        this.muleClient = muleClient;
    }
}
