package com.armedia.acm.services.search.web.api;

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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by marjan.stefanoski on 18.12.2014.
 */
@Controller
@RequestMapping( { "/api/v1/plugin/search", "/api/latest/plugin/search"} )
public class FacetedComplaintSearchAPIController {

    private Logger log = LoggerFactory.getLogger(getClass());

    private MuleClient muleClient;

    @RequestMapping(value = "/facetedComplaintSearch", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String facetedComplaintSearch (
            @RequestParam(value = "createDate", required = false) Date createDate,
            @RequestParam(value = "modifyDate", required = false) Date modifyDate,
            @RequestParam(value = "dueDate", required = false) Date dueDate,
            @RequestParam(value = "incidentDate", required = false) Date incidentDate,
            @RequestParam(value = "assigneeFullName", required = false) String assigneeFullName,
            @RequestParam(value = "creator", required = false) String creator,
            @RequestParam(value = "modifier", required = false) String modifier,
            @RequestParam(value = "priority", required = false) String priority,
            @RequestParam(value = "incidentType", required = false) String incidentType,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,
            Authentication authentication,
            HttpServletResponse httpResponse
    ) throws MuleException, Exception {
        if ( log.isDebugEnabled() ) {
            log.debug("User '" + authentication.getName() + "' is performing faceted search for objects of type: Complaint' ");
        }
        String q = URLEncoder.encode("*:*")+"&fq=object_type_s:COMPLAINT&facet=true&facet.field=object_type_s";
        String sort= "";

        Map<String, Object> headers = new HashMap<>();
        headers.put("query", q);
        headers.put("firstRow", startRow);
        headers.put("maxRows", maxRows);
        headers.put("sort", sort);

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
