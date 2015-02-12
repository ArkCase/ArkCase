package com.armedia.acm.services.search.service;

import com.armedia.acm.services.search.model.SolrCore;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by armdev on 2/12/15.
 */
public class SolrSearchService
{
    private MuleClient muleClient;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    public String search(Authentication authentication, SolrCore core, String query, int startRow, int maxRows,
                         String sort)
            throws MuleException, IllegalStateException
    {
        return search(authentication, core, query, startRow, maxRows, sort, "");
    }

    public String search(Authentication authentication, SolrCore core, String query, int startRow,
                         int maxRows, String sort, String rowQueryParametars)
            throws MuleException, IllegalStateException
    {
        Map<String, Object> headers = new HashMap<>();
        headers.put("query", query);
        headers.put("firstRow", startRow);
        headers.put("maxRows", maxRows);
        headers.put("sort", sort);
        headers.put("acmUser", authentication);
        headers.put("rowQueryParametars", rowQueryParametars);

        MuleMessage response = getMuleClient().send(core.getMuleEndpointUrl(), "", headers);

        log.debug("Response type: " + response.getPayload().getClass());

        if ( response.getPayload() instanceof String )
        {
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
