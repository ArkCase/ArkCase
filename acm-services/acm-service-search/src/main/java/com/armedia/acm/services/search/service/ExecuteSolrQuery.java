package com.armedia.acm.services.search.service;

import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.services.search.model.SolrCore;

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
public class ExecuteSolrQuery
{

    private Logger log = LoggerFactory.getLogger(getClass());

    private MuleContextManager muleContextManager;

    public String getResultsByPredefinedQuery(Authentication auth, SolrCore core, String solrQuery, int firstRow, int maxRows, String sort)
            throws MuleException
    {
        return getResultsByPredefinedQuery(auth, core, solrQuery, firstRow, maxRows, sort, true);
    }

    public String getResultsByPredefinedQuery(Authentication auth, SolrCore core, String solrQuery, int firstRow, int maxRows, String sort,
            boolean indent) throws MuleException
    {
        return getResultsByPredefinedQuery(auth, core, solrQuery, firstRow, maxRows, sort, indent, "", true, false);
    }

    public String getResultsByPredefinedQuery(Authentication auth, SolrCore core, String solrQuery, int firstRow, int maxRows, String sort,
            String rowQueryParameters) throws MuleException
    {
        return getResultsByPredefinedQuery(auth, core, solrQuery, firstRow, maxRows, sort, true, rowQueryParameters);
    }

    public String getResultsByPredefinedQuery(Authentication auth, SolrCore core, String solrQuery, int firstRow, int maxRows, String sort,
            boolean indent, String rowQueryParameters) throws MuleException
    {
        return getResultsByPredefinedQuery(auth, core, solrQuery, firstRow, maxRows, sort, indent, rowQueryParameters, true, false);
    }

    public String getResultsByPredefinedQuery(Authentication auth, SolrCore core, String solrQuery, int firstRow, int maxRows, String sort,
            String rowQueryParameters, boolean filterParentRef) throws MuleException
    {
        return getResultsByPredefinedQuery(auth, core, solrQuery, firstRow, maxRows, sort, true, rowQueryParameters, filterParentRef);
    }

    public String getResultsByPredefinedQuery(Authentication auth, SolrCore core, String solrQuery, int firstRow, int maxRows, String sort,
            boolean indent, String rowQueryParameters, boolean filterParentRef) throws MuleException
    {
        return getResultsByPredefinedQuery(auth, core, solrQuery, firstRow, maxRows, sort, indent, rowQueryParameters, filterParentRef,
                false);
    }

    public String getResultsByPredefinedQuery(Authentication auth, SolrCore core, String solrQuery, int firstRow, int maxRows, String sort,
            String rowQueryParameters, boolean filterParentRef, boolean filterSubscriptionEvents) throws MuleException
    {
        return getResultsByPredefinedQuery(auth, core, solrQuery, firstRow, maxRows, sort, true, rowQueryParameters, filterParentRef,
                filterSubscriptionEvents);
    }

    public String getResultsByPredefinedQuery(Authentication auth, SolrCore core, String solrQuery, int firstRow, int maxRows, String sort,
            boolean indent, String rowQueryParameters, boolean filterParentRef, boolean filterSubscriptionEvents) throws MuleException
    {
        Map<String, Object> headers = new HashMap<>();
        headers.put("query", solrQuery);
        headers.put("firstRow", firstRow);
        headers.put("maxRows", maxRows);
        headers.put("sort", sort);
        headers.put("acmUser", auth);
        headers.put("filterParentRef", filterParentRef);
        headers.put("filterSubscriptionEvents", filterSubscriptionEvents);
        headers.put("rowQueryParametars", rowQueryParameters);
        headers.put("indent", indent ? indent : "");

        MuleMessage response = getMuleContextManager().send(core.getMuleEndpointUrl(), "", headers);

        log.debug("Response type: " + response.getPayload().getClass());

        if (response.getPayload() instanceof String)
        {
            return indent ? ((String) response.getPayload()) : ((String) response.getPayload()).trim();
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
