package com.armedia.acm.services.search.service;

import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.services.search.model.SolrCore;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
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

    private MuleContextManager muleContextManager;

    public String getResultsByPredefinedQuery( Authentication auth, SolrCore core, String solrQuery, int firstRow,
                                               int maxRows, String sort ) throws MuleException
    {
        String results = getResultsByPredefinedQuery(auth, core, solrQuery, firstRow, maxRows, sort, "");
        return results;
    }

    public String getResultsByPredefinedQuery( Authentication auth, SolrCore core, String solrQuery, int firstRow,
                                               int maxRows, String sort, String rowQueryParameters ) throws MuleException
    {
        Map<String, Object> headers = new HashMap<>();
        headers.put("query", solrQuery);
        headers.put("firstRow", firstRow);
        headers.put("maxRows", maxRows);
        headers.put("sort", sort);
        headers.put("acmUser", auth);
        headers.put("rowQueryParametars", rowQueryParameters);

        MuleMessage request = new DefaultMuleMessage("", headers, getMuleContextManager().getMuleContext());
        MuleMessage response = getMuleContextManager().getMuleClient().send(core.getMuleEndpointUrl(), request);

        log.debug("Response type: " + response.getPayload().getClass());

        if ( response.getPayload() instanceof String )
        {
            return (String) response.getPayload();
        }

        throw new IllegalStateException("Unexpected payload type: " + response.getPayload().getClass().getName());
    }

    public MuleContextManager getMuleContextManager()
    {
        return muleContextManager;
    }

    public void setMuleContextManager(MuleContextManager muleContextManager)
    {
        this.muleContextManager = muleContextManager;
    }
}
