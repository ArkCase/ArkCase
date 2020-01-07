package com.armedia.acm.services.search.service;

/*-
 * #%L
 * ACM Service: Search
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software.
 *
 * If the software was purchased under a paid ArkCase license, the terms of
 * the paid license agreement will prevail.  Otherwise, the software is
 * provided under the following open source license terms:
 *
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.core.AcmUserAuthorityContext;
import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.services.search.model.SearchConstants;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.model.solr.SolrDeleteDocumentsByQueryRequest;
import com.armedia.acm.services.search.model.solr.SolrDocumentsQuery;
import com.armedia.acm.web.api.MDCConstants;
import com.google.common.base.Strings;

import org.apache.commons.lang3.StringUtils;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Created by marjan.stefanoski on 02.02.2015.
 */
public class ExecuteSolrQuery
{

    private Logger log = LogManager.getLogger(getClass());

    private MuleContextManager muleContextManager;
    private ObjectConverter objectConverter;
    private SendDocumentsToSolr sendDocumentsToSolr;

    /**
     * Executes solr queries and returns results as String
     *
     * @param auth
     *            Authenticated user
     * @param core
     *            SolrCore could be quick or advanced search
     * @param solrQuery
     *            actual query
     * @param firstRow
     *            starting row
     * @param maxRows
     *            how many rows to return
     * @param sort
     *            sort by which field
     * @return results as String
     * @throws MuleException
     */
    public String getResultsByPredefinedQuery(Authentication auth, SolrCore core, String solrQuery, int firstRow, int maxRows, String sort)
            throws MuleException
    {
        return getResultsByPredefinedQuery(auth, core, solrQuery, firstRow, maxRows, sort, true);
    }

    /**
     * Executes solr query asynchronously in separate thread and returns results as String
     *
     * @param auth
     *            Authenticated user
     * @param core
     *            SolrCore could be quick or advanced search
     * @param solrQuery
     *            actual query
     * @param firstRow
     *            starting row
     * @param maxRows
     *            how many rows to return
     * @param sort
     *            sort by which field
     * @return results as String in CompletableFuture
     * @throws MuleException
     */
    @Async
    public CompletableFuture<String> getResultsByPredefinedQueryAsync(Authentication auth, SolrCore core, String solrQuery, int firstRow,
            int maxRows, String sort)
            throws MuleException
    {
        return CompletableFuture.completedFuture(
                getResultsByPredefinedQuery(auth, core, solrQuery, firstRow, maxRows, sort, true, "", true, false, "", false, ""));
    }

    /**
     * Executes solr queries and returns results as String
     *
     * @param auth
     *            Authenticated user
     * @param core
     *            SolrCore could be quick or advanced search
     * @param solrQuery
     *            actual query
     * @param firstRow
     *            starting row
     * @param maxRows
     *            how many rows to return
     * @param sort
     *            sort by which field
     * @param indent
     *            boolean whether results should be indented
     * @return results as String
     * @throws MuleException
     */
    public String getResultsByPredefinedQuery(Authentication auth, SolrCore core, String solrQuery, int firstRow, int maxRows, String sort,
            boolean indent) throws MuleException
    {
        return getResultsByPredefinedQuery(auth, core, solrQuery, firstRow, maxRows, sort, indent, "", true, false);
    }

    /**
     * Executes solr queries and returns results as String
     *
     * @param auth
     *            Authenticated user
     * @param core
     *            SolrCore could be quick or advanced search
     * @param solrQuery
     *            actual query
     * @param firstRow
     *            starting row
     * @param maxRows
     *            how many rows to return
     * @param sort
     *            sort by which field
     * @param rowQueryParameters
     *            row query parameters
     * @return results as String
     * @throws MuleException
     */
    public String getResultsByPredefinedQuery(Authentication auth, SolrCore core, String solrQuery, int firstRow, int maxRows, String sort,
            String rowQueryParameters) throws MuleException
    {
        return getResultsByPredefinedQuery(auth, core, solrQuery, firstRow, maxRows, sort, true, rowQueryParameters);
    }

    /**
     * Executes solr queries and returns results as String
     *
     * @param auth
     *            Authenticated user
     * @param core
     *            SolrCore could be quick or advanced search
     * @param solrQuery
     *            actual query
     * @param firstRow
     *            starting row
     * @param maxRows
     *            how many rows to return
     * @param sort
     *            sort by which field
     * @param indent
     *            boolean whether results should be indented
     * @param rowQueryParameters
     *            row query parameters
     * @return results as String
     * @throws MuleException
     */
    public String getResultsByPredefinedQuery(Authentication auth, SolrCore core, String solrQuery, int firstRow, int maxRows, String sort,
            boolean indent, String rowQueryParameters) throws MuleException
    {
        return getResultsByPredefinedQuery(auth, core, solrQuery, firstRow, maxRows, sort, indent, rowQueryParameters, true, false);
    }

    /**
     * Executes solr queries and returns results as String
     *
     * @param auth
     *            Authenticated user
     * @param core
     *            SolrCore could be quick or advanced search
     * @param solrQuery
     *            actual query
     * @param firstRow
     *            starting row
     * @param maxRows
     *            how many rows to return
     * @param sort
     *            sort by which field
     * @param rowQueryParameters
     *            row query parameters
     * @param filterParentRef
     *            filterParentRef
     * @return results as String
     * @throws MuleException
     */
    public String getResultsByPredefinedQuery(Authentication auth, SolrCore core, String solrQuery, int firstRow, int maxRows, String sort,
            String rowQueryParameters, boolean filterParentRef) throws MuleException
    {
        return getResultsByPredefinedQuery(auth, core, solrQuery, firstRow, maxRows, sort, true, rowQueryParameters, filterParentRef);
    }

    /**
     * Executes solr queries and returns results as String
     *
     * @param auth
     *            Authenticated user
     * @param core
     *            SolrCore could be quick or advanced search
     * @param solrQuery
     *            actual query
     * @param firstRow
     *            starting row
     * @param maxRows
     *            how many rows to return
     * @param sort
     *            sort by which field
     * @param indent
     *            boolean whether results should be indented
     * @param rowQueryParameters
     *            row query parameters
     * @param filterParentRef
     *            filterParentRef
     * @return results as String
     * @throws MuleException
     */
    public String getResultsByPredefinedQuery(Authentication auth, SolrCore core, String solrQuery, int firstRow, int maxRows, String sort,
            boolean indent, String rowQueryParameters, boolean filterParentRef) throws MuleException
    {
        return getResultsByPredefinedQuery(auth, core, solrQuery, firstRow, maxRows, sort, indent, rowQueryParameters, filterParentRef,
                false);
    }

    /**
     * Executes solr queries and returns results as String
     *
     * @param auth
     *            Authenticated user
     * @param core
     *            SolrCore could be quick or advanced search
     * @param solrQuery
     *            actual query
     * @param firstRow
     *            starting row
     * @param maxRows
     *            how many rows to return
     * @param sort
     *            sort by which field
     * @param rowQueryParameters
     *            row query parameters
     * @param filterParentRef
     *            filterParentRef
     * @return results as String
     * @throws MuleException
     */
    public String getResultsByPredefinedQuery(Authentication auth, SolrCore core, String solrQuery, int firstRow, int maxRows, String sort,
            String rowQueryParameters, boolean filterParentRef, boolean filterSubscriptionEvents) throws MuleException
    {
        return getResultsByPredefinedQuery(auth, core, solrQuery, firstRow, maxRows, sort, true, rowQueryParameters, filterParentRef,
                filterSubscriptionEvents);
    }

    /**
     * Executes solr queries and returns results as String
     *
     * @param auth
     *            Authenticated user
     * @param core
     *            SolrCore could be quick or advanced search
     * @param solrQuery
     *            actual query
     * @param firstRow
     *            starting row
     * @param maxRows
     *            how many rows to return
     * @param sort
     *            sort by which field
     * @param indent
     *            boolean whether results should be indented
     * @param rowQueryParameters
     *            row query parameters
     * @param filterParentRef
     *            filterParentRef
     * @param filterSubscriptionEvents
     *            boolean whether should filter subscription events
     * @return results as String
     * @throws MuleException
     */
    public String getResultsByPredefinedQuery(Authentication auth, SolrCore core, String solrQuery, int firstRow, int maxRows, String sort,
            boolean indent, String rowQueryParameters, boolean filterParentRef, boolean filterSubscriptionEvents) throws MuleException
    {
        return getResultsByPredefinedQuery(auth, core, solrQuery, firstRow, maxRows, sort, indent, rowQueryParameters, filterParentRef,
                filterSubscriptionEvents, SearchConstants.DEFAULT_FIELD);
    }

    public String getResultsByPredefinedQuery(Authentication auth, SolrCore core, String solrQuery, int firstRow, int maxRows, String sort,
            boolean indent, String rowQueryParameters, boolean filterParentRef, boolean filterSubscriptionEvents, String defaultField)
            throws MuleException
    {
        return getResultsByPredefinedQuery(auth, core, solrQuery, firstRow, maxRows, sort, indent, rowQueryParameters, filterParentRef,
                filterSubscriptionEvents, defaultField, null);
    }

    /**
     * Executes solr queries and returns results as String
     *
     * @param auth
     *            Authenticated user
     * @param core
     *            SolrCore could be quick or advanced search
     * @param solrQuery
     *            actual query
     * @param firstRow
     *            starting row
     * @param maxRows
     *            how many rows to return
     * @param sort
     *            sort by which field
     * @param indent
     *            boolean whether results should be indented
     * @param rowQueryParameters
     *            row query parameters
     * @param filterParentRef
     *            filterParentRef
     * @param filterSubscriptionEvents
     *            boolean whether should filter subscription events
     * @param defaultField
     *            which default filed to be set. Can be null(than default field defined in solrconfig.xml is used)
     * @return results as String
     * @throws MuleException
     */
    public String getResultsByPredefinedQuery(Authentication auth, SolrCore core, String solrQuery, int firstRow, int maxRows, String sort,
            boolean indent, String rowQueryParameters, boolean filterParentRef, boolean filterSubscriptionEvents, String defaultField,
            String fields)
            throws MuleException
    {
        return getResultsByPredefinedQuery(auth, core, solrQuery, firstRow, maxRows, sort, indent, rowQueryParameters, filterParentRef,
                filterSubscriptionEvents, defaultField, true, fields);
    }

    /**
     *
     * Deprecated since parameter filterParentRef is no longer used.
     * 
     * @see ExecuteSolrQuery#getResultsByPredefinedQuery(Authentication, SolrCore, String, int, int, String, String,
     *      boolean)
     *
     *      Executes solr queries and returns results as String
     *
     * @param authentication
     *            Authenticated user
     * @param core
     *            SolrCore could be quick or advanced search
     * @param solrQuery
     *            actual query
     * @param firstRow
     *            starting row
     * @param maxRows
     *            how many rows to return
     * @param sort
     *            sort by which field
     * @param indent
     *            boolean whether results should be indented
     * @param rowQueryParameters
     *            row query parameters
     * @param filterParentRef
     *            filterParentRef
     * @param filterSubscriptionEvents
     *            boolean whether should filter subscription events
     * @param defaultField
     *            which default filed to be set. Can be null(than default field defined in solrconfig.xml is used)
     * @param includeDACFilter
     *            boolean whether should add acl filters on solr query
     * @return results as String
     * @throws MuleException
     */
    @Deprecated
    public String getResultsByPredefinedQuery(Authentication authentication, SolrCore core, String solrQuery, int firstRow, int maxRows,
            String sort, boolean indent, String rowQueryParameters, boolean filterParentRef, boolean filterSubscriptionEvents,
            String defaultField, boolean includeDACFilter, String fields) throws MuleException
    {
        return getResultsByPredefinedQuery(authentication, core, solrQuery, firstRow, maxRows, sort, indent, rowQueryParameters,
                filterSubscriptionEvents, defaultField, includeDACFilter, false, false, fields);
    }

    /**
     *
     * Executes solr queries and returns results as String
     *
     * @param authentication
     *            Authenticated user
     * @param core
     *            SolrCore could be quick or advanced search
     * @param solrQuery
     *            actual query
     * @param firstRow
     *            starting row
     * @param maxRows
     *            how many rows to return
     * @param sort
     *            sort by which field
     * @param indent
     *            boolean whether results should be indented
     * @param rowQueryParameters
     *            row query parameters
     * @param filterSubscriptionEvents
     *            boolean whether should filter subscription events
     * @param defaultField
     *            which default filed to be set. Can be null(than default field defined in solrconfig.xml is used)
     * @param includeDACFilter
     *            boolean whether should add acl filters on solr query
     * @return results as String
     */
    public String getResultsByPredefinedQuery(Authentication authentication, SolrCore core, String solrQuery, int firstRow, int maxRows,
            String sort, boolean indent, String rowQueryParameters, boolean filterSubscriptionEvents,
            String defaultField, boolean includeDACFilter) throws MuleException
    {
        return getResultsByPredefinedQuery(authentication, core, solrQuery, firstRow, maxRows, sort, indent, rowQueryParameters,
                filterSubscriptionEvents, defaultField, includeDACFilter, false, false, null);
    }

    public String getResultsByPredefinedQuery(Authentication authentication, SolrCore core, String solrQuery, int firstRow,
            int maxRows, String sort, boolean indent, String rowQueryParameters,
            boolean filterSubscriptionEvents, String defaultField, boolean includeDACFilter,
            boolean includeDenyAccessFilter, boolean enableDocumentACL) throws MuleException
    {
        return getResultsByPredefinedQuery(authentication, core, solrQuery, firstRow, maxRows, sort, indent, rowQueryParameters,
                filterSubscriptionEvents, defaultField, includeDACFilter, includeDenyAccessFilter, enableDocumentACL, null);
    }

    public String getResultsByPredefinedQuery(Authentication authentication, SolrCore core, String solrQuery, int firstRow,
            int maxRows, String sort, boolean indent, String rowQueryParameters,
            boolean filterSubscriptionEvents, String defaultField, boolean includeDACFilter,
            boolean includeDenyAccessFilter, boolean enableDocumentACL, String fields) throws MuleException
    {
        AcmUserAuthorityContext authorityContext = (AcmUserAuthorityContext) authentication;
        Map<String, Object> headers = new HashMap<>();
        headers.put("query", solrQuery);
        headers.put("firstRow", firstRow);
        headers.put("maxRows", maxRows);
        headers.put("sort", sort);
        headers.put("acmUser", authorityContext.getUserIdentity());
        headers.put("acmUserGroupIds", authorityContext.getGroupAuthorities());
        headers.put("filterSubscriptionEvents", filterSubscriptionEvents);
        headers.put("rowQueryParametars", rowQueryParameters);
        headers.put("enableDocumentACL", includeDenyAccessFilter);
        headers.put("includeDenyAccessFilter", enableDocumentACL);
        headers.put("indent", indent ? true : "");
        headers.put("df", defaultField);
        if (!Strings.isNullOrEmpty(fields))
        {
            headers.put("fl", fields);
        }
        else
        {
            headers.put("fl", "");
        }
        headers.put("includeDACFilter", includeDACFilter);

        /*
         * AFDP-7210 The Mule HTTP transport somehow clears the MDC context. This send() call
         * is the only use of Mule HTTP transport in ArkCase. I fix the issue by saving and then
         * restoring the MDC variables. If we were going to keep Mule I would find a better
         * solution. But seeing this code will remind us to remove Mule from our solution.
         */
        String alfrescoUser = MDC.get(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY);
        String requestId = MDC.get(MDCConstants.EVENT_MDC_REQUEST_ID_KEY);
        String pentahoUser = MDC.get(MDCConstants.EVENT_MDC_REQUEST_PENTAHO_USER_ID_KEY);
        String remoteAddress = MDC.get(MDCConstants.EVENT_MDC_REQUEST_REMOTE_ADDRESS_KEY);
        String userId = MDC.get(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY);

        MuleMessage response = getMuleContextManager().send(core.getMuleEndpointUrl(), "", headers);

        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, alfrescoUser);
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, requestId);
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_PENTAHO_USER_ID_KEY, pentahoUser);
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_REMOTE_ADDRESS_KEY, remoteAddress);
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY, userId);

        log.trace("Response type: {}", response.getPayload().getClass());

        if (response.getPayload() instanceof String)
        {
            return indent ? ((String) response.getPayload()) : ((String) response.getPayload()).trim();
        }

        throw new IllegalStateException("Unexpected payload type: " + response.getPayload().getClass().getName());
    }

    /**
     * This method executes delete of documents in solr using search query
     * (Beware) it could easily delete all documents if query is not specific
     *
     * @param queueName
     *            Which queue to send the request to
     * @param query
     *            Solr Search query for removing found documents
     * @throws MuleException
     */
    public void sendSolrDeleteQuery(String queueName, String query) throws MuleException
    {
        log.debug("Received query [{}] for deletion.", query);

        if (StringUtils.isNotEmpty(query))
        {
            SolrDocumentsQuery documentsQuery = new SolrDocumentsQuery();
            documentsQuery.setQuery(query);
            SolrDeleteDocumentsByQueryRequest deleteQueryRequest = new SolrDeleteDocumentsByQueryRequest(documentsQuery);
            String json = objectConverter.getJsonMarshaller().marshal(deleteQueryRequest);
            getSendDocumentsToSolr().sendSolrDocuments(queueName, json);
        }
        else
        {
            throw new IllegalArgumentException("query must not be empty or null.");
        }
    }

    public MuleContextManager getMuleContextManager()
    {
        return muleContextManager;
    }

    public void setMuleContextManager(MuleContextManager muleContextManager)
    {
        this.muleContextManager = muleContextManager;
    }

    public void setObjectConverter(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
    }

    public SendDocumentsToSolr getSendDocumentsToSolr()
    {
        return sendDocumentsToSolr;
    }

    public void setSendDocumentsToSolr(SendDocumentsToSolr sendDocumentsToSolr)
    {
        this.sendDocumentsToSolr = sendDocumentsToSolr;
    }
}
