package com.armedia.acm.services.search.web.api;

import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by marjan.stefanoski on 17.12.2014.
 */
@Controller
@RequestMapping( { "/api/v1/plugin/search", "/api/latest/plugin/search"} )
public class FacetedSearchAPIController {

    private Logger log = LoggerFactory.getLogger(getClass());

    private MuleClient muleClient;

    @RequestMapping(value = "/facetedSearch", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String mainNotFilteredFacetedSerach(
            @RequestParam(value = "q", required = true) String q,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "100") int maxRows,
            Authentication authentication,
            HttpServletResponse httpResponse
    ) throws MuleException {

        if ( log.isDebugEnabled() ) {
            log.debug("User '" + authentication.getName() + "' is performing facet search for the query: '" + q + "' ");
        }

        String query = URLEncoder.encode(q)+"&facet=true&facet.field=object_type_s";
        String sort= "";

        Map<String, Object> headers = new HashMap<>();
        headers.put("query", query);
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
