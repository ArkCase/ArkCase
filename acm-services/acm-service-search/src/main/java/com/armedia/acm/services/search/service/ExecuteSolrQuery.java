package com.armedia.acm.services.search.service;

import com.armedia.acm.pluginmanager.service.AcmPluginManager;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by marjan.stefanoski on 02.02.2015.
 */
public class ExecuteSolrQuery {


    private Logger log = LoggerFactory.getLogger(getClass());

    private MuleClient muleClient;

    public String getResultsByPredefinedQuery( String solrQuery, String firstRow, String maxRows, String sort, Authentication auth ) throws MuleException {

        Map<String, Object> headers = new HashMap<>();
        headers.put("query", solrQuery);
        headers.put("firstRow", firstRow);
        headers.put("maxRows", maxRows);
        headers.put("sort", sort);
        headers.put("acmUser", auth);

        MuleMessage response = getMuleClient().send("vm://quickSearchQuery.in", "", headers);

        log.debug("Response type: " + response.getPayload().getClass());

        if ( response.getPayload() instanceof String )
        {
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
