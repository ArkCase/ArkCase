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

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping( { "/api/v1/plugin/search", "/api/latest/plugin/search"} )
public class PersonSearchByNameAndContactMethodAPIController
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private MuleClient muleClient;

    @RequestMapping(value = "/personSearch", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String person(
            @RequestParam(value = "name", required = true) String personName,
            @RequestParam(value = "cm", required = true) String contactMethod,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,
            Authentication authentication,
            HttpServletResponse httpResponse
    ) throws MuleException
    {
        if ( log.isDebugEnabled() )
        {
            log.debug("User '" + authentication.getName() + "' is searching for name '" + personName + "' "
                + ", contact method value: '" + contactMethod + "'");
        }

        // use a SOLR join query to restrict person results to persons with a contact method matching the
        // given value.
        final String encodedContactMethodJoin = URLEncoder.encode("{!join from=id to=contact_method_ss}");

        String query = "object_type_s:PERSON AND name:" + URLEncoder.encode(personName) + " AND " +
                encodedContactMethodJoin + "value_parseable:" + URLEncoder.encode(contactMethod);
        String sort = "last_name_lcs ASC, first_name_lcs ASC";

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

        if ( response.getPayload() instanceof String )
        {
        	httpResponse.addHeader("X-JSON", response.getPayload().toString());
            return (String) response.getPayload();
        }

        throw new IllegalStateException("Unexpected payload type: " + response.getPayload().getClass().getName());
    }

    public MuleClient getMuleClient()
    {
        return muleClient;
    }

    public void setMuleClient(MuleClient muleClient)
    {
        this.muleClient = muleClient;
    }
}
