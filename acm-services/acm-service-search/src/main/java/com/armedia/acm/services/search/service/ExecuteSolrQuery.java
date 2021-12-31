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

import static com.armedia.acm.services.search.model.solr.SolrConstants.SOLR_PARAM_DEFAULT_FIELD;
import static com.armedia.acm.services.search.model.solr.SolrConstants.SOLR_PARAM_INDENT;
import static com.armedia.acm.services.search.model.solr.SolrConstants.SOLR_PARAM_RESPONSE;
import static com.armedia.acm.services.search.model.solr.SolrConstants.SOLR_PARAM_WRITER;
import static com.armedia.acm.services.search.model.solr.SolrConstants.SOLR_RAW_QUERY_PARAM_SPLITTER;
import static com.armedia.acm.services.search.model.solr.SolrConstants.SOLR_RAW_QUERY_SPLITTER;
import static com.armedia.acm.services.search.model.solr.SolrConstants.SOLR_SORT_CLAUSE_SPLITTER;
import static com.armedia.acm.services.search.model.solr.SolrConstants.SOLR_WRITER_JSON;

import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.services.search.exception.SolrException;
import com.armedia.acm.services.search.model.QueryParameter;
import com.armedia.acm.services.search.model.SearchConstants;
import com.armedia.acm.services.search.model.solr.ArkCaseSolrUtils;
import com.armedia.acm.services.search.model.solr.SolrConfig;
import com.armedia.acm.services.search.model.solr.SolrCore;
import com.armedia.acm.services.search.model.solr.SolrDataAccessOptions;
import com.armedia.acm.services.search.model.solr.SolrDeleteDocumentsByQueryRequest;
import com.armedia.acm.services.search.model.solr.SolrDocumentsQuery;
import com.armedia.acm.services.search.service.solr.SolrClientProvider;
import com.armedia.acm.services.search.util.SolrDataAccessFilterUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.util.NamedList;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Created by marjan.stefanoski on 02.02.2015.
 */
public class ExecuteSolrQuery
{

    private Logger log = LogManager.getLogger(getClass());

    private ObjectConverter objectConverter;
    private SendDocumentsToSolr sendDocumentsToSolr;
    private SolrClientProvider solrClientProvider;
    private SolrConfig configuration;

    /**
     * Executes solr queries and returns results as String
     *
     * @param auth
     *            Authenticated user
     * @param core
     *            SolrCore could be advanced search, or case file search
     * @param solrQuery
     *            actual query
     * @param firstRow
     *            starting row
     * @param maxRows
     *            how many rows to return
     * @param sort
     *            sort by which field
     * @return results as String
     * @throws SolrException
     */
    public String getResultsByPredefinedQuery(Authentication auth, SolrCore core, String solrQuery, int firstRow, int maxRows, String sort)
            throws SolrException
    {
        return getResultsByPredefinedQuery(auth, core, solrQuery, firstRow, maxRows, sort, true);
    }

    /**
     * Executes solr query asynchronously in separate thread and returns results as String
     *
     * @param auth
     *            Authenticated user
     * @param core
     *            SolrCore could be advanced search, or case file search
     * @param solrQuery
     *            actual query
     * @param firstRow
     *            starting row
     * @param maxRows
     *            how many rows to return
     * @param sort
     *            sort by which field
     * @return results as String in CompletableFuture
     * @throws SolrException
     */
    @Async
    public CompletableFuture<String> getResultsByPredefinedQueryAsync(Authentication auth, SolrCore core, String solrQuery, int firstRow,
            int maxRows, String sort)
            throws SolrException
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
     *            SolrCore could be advanced search, or case file search
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
     * @throws SolrException
     */
    public String getResultsByPredefinedQuery(Authentication auth, SolrCore core, String solrQuery, int firstRow, int maxRows, String sort,
            boolean indent) throws SolrException
    {
        return getResultsByPredefinedQuery(auth, core, solrQuery, firstRow, maxRows, sort, indent, "", true, false);
    }

    /**
     * Executes solr queries and returns results as String
     *
     * @param auth
     *            Authenticated user
     * @param core
     *            SolrCore could be advanced search, or case file search
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
     * @throws SolrException
     */
    public String getResultsByPredefinedQuery(Authentication auth, SolrCore core, String solrQuery, int firstRow, int maxRows, String sort,
            String rowQueryParameters) throws SolrException
    {
        return getResultsByPredefinedQuery(auth, core, solrQuery, firstRow, maxRows, sort, true, rowQueryParameters);
    }

    /**
     * Executes solr queries and returns results as String
     *
     * @param auth
     *            Authenticated user
     * @param core
     *            SolrCore could be advanced search, or case file search
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
     * @throws SolrException
     */
    public String getResultsByPredefinedQuery(Authentication auth, SolrCore core, String solrQuery, int firstRow, int maxRows, String sort,
            boolean indent, String rowQueryParameters) throws SolrException
    {
        return getResultsByPredefinedQuery(auth, core, solrQuery, firstRow, maxRows, sort, indent, rowQueryParameters, true, false);
    }

    /**
     * Executes solr queries and returns results as String
     *
     * @param auth
     *            Authenticated user
     * @param core
     *            SolrCore could be advanced search, or case file search
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
     * @throws SolrException
     */
    public String getResultsByPredefinedQuery(Authentication auth, SolrCore core, String solrQuery, int firstRow, int maxRows, String sort,
            String rowQueryParameters, boolean filterParentRef) throws SolrException
    {
        return getResultsByPredefinedQuery(auth, core, solrQuery, firstRow, maxRows, sort, true, rowQueryParameters, filterParentRef);
    }

    /**
     * Executes solr queries and returns results as String
     *
     * @param auth
     *            Authenticated user
     * @param core
     *            SolrCore could be advanced search, or case file search
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
     * @throws SolrException
     */
    public String getResultsByPredefinedQuery(Authentication auth, SolrCore core, String solrQuery, int firstRow, int maxRows, String sort,
            boolean indent, String rowQueryParameters, boolean filterParentRef) throws SolrException
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
     *            SolrCore could be advanced search, or case file search
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
     * @throws SolrException
     */
    public String getResultsByPredefinedQuery(Authentication auth, SolrCore core, String solrQuery, int firstRow, int maxRows, String sort,
            String rowQueryParameters, boolean filterParentRef, boolean filterSubscriptionEvents) throws SolrException
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
     *            SolrCore could be advanced search, or case file search
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
     * @throws SolrException
     */
    public String getResultsByPredefinedQuery(Authentication auth, SolrCore core, String solrQuery, int firstRow, int maxRows, String sort,
            boolean indent, String rowQueryParameters, boolean filterParentRef, boolean filterSubscriptionEvents) throws SolrException
    {
        return getResultsByPredefinedQuery(auth, core, solrQuery, firstRow, maxRows, sort, indent, rowQueryParameters, filterParentRef,
                filterSubscriptionEvents, SearchConstants.DEFAULT_FIELD);
    }

    public String getResultsByPredefinedQuery(Authentication auth, SolrCore core, String solrQuery, int firstRow, int maxRows, String sort,
            boolean indent, String rowQueryParameters, boolean filterParentRef, boolean filterSubscriptionEvents, String defaultField)
            throws SolrException
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
     *            SolrCore could be advanced search, or case file search
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
     * @throws SolrException
     */
    public String getResultsByPredefinedQuery(Authentication auth, SolrCore core, String solrQuery, int firstRow, int maxRows, String sort,
            boolean indent, String rowQueryParameters, boolean filterParentRef, boolean filterSubscriptionEvents, String defaultField,
            String fields)
            throws SolrException
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
     *            SolrCore could be advanced search, or case file search
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
     * @throws SolrException
     */
    @Deprecated
    public String getResultsByPredefinedQuery(Authentication authentication, SolrCore core, String solrQuery, int firstRow, int maxRows,
            String sort, boolean indent, String rowQueryParameters, boolean filterParentRef, boolean filterSubscriptionEvents,
            String defaultField, boolean includeDACFilter, String fields) throws SolrException
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
     *            SolrCore could be advanced search, or case file search
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
            String defaultField, boolean includeDACFilter) throws SolrException
    {
        return getResultsByPredefinedQuery(authentication, core, solrQuery, firstRow, maxRows, sort, indent, rowQueryParameters,
                filterSubscriptionEvents, defaultField, includeDACFilter, false, false, null);
    }

    public String getResultsByPredefinedQuery(Authentication authentication, SolrCore core, String solrQuery, int firstRow,
            int maxRows, String sort, boolean indent, String rowQueryParameters,
            boolean filterSubscriptionEvents, String defaultField, boolean includeDACFilter,
            boolean includeDenyAccessFilter, boolean enableDocumentACL) throws SolrException
    {
        return getResultsByPredefinedQuery(authentication, core, solrQuery, firstRow, maxRows, sort, indent, rowQueryParameters,
                filterSubscriptionEvents, defaultField, includeDACFilter, includeDenyAccessFilter, enableDocumentACL, null);
    }

    public String getResultsByPredefinedQuery(Authentication authentication, SolrCore core, String solrQuery, int firstRow,
            int maxRows, String sort, boolean indent, String rowQueryParameters,
            boolean filterSubscriptionEvents, String defaultField, boolean includeDACFilter,
            boolean includeDenyAccessFilter, boolean enableDocumentACL, String fields) throws SolrException
    {
        SolrClient client = getSolrClientProvider().getClient();
        if (client == null)
        {
            throw new SolrException("Unable to send Solr Request, invalid client");
        }
        log.trace("Using SolrClient: [{}]", client.getClass().getName());

        SolrQuery query = buildQuery(authentication, solrQuery, firstRow, maxRows, sort, indent, rowQueryParameters,
                filterSubscriptionEvents, defaultField, includeDACFilter, includeDenyAccessFilter, enableDocumentACL, fields);
        log.trace("Using SolrQuery: [{}]", query);

        if (query.toQueryString().contains("{!join"))
        {
            log.error("Found query using joins!: [{}]", query.toQueryString());
        }

        String collection = mapCollection(core, getConfiguration(), query);
        log.trace("Using Core/Collection: [{}]", collection);

        try
        {
            SolrRequest solrRequest = ArkCaseSolrUtils.configureRequest(new QueryRequest(query, SolrRequest.METHOD.GET));

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            NamedList<Object> response = client.request(solrRequest, collection);
            stopWatch.stop();

            log.trace("Received response from Solr in {}ms", stopWatch.getTotalTimeMillis());
            log.trace("Received response from Solr: [{}]", response);
            return (String) response.get(SOLR_PARAM_RESPONSE);
        }
        catch (SolrServerException | IOException e)
        {
            log.error("Unable to process query, server error: [{}]", query, e);
            throw new SolrException("Unable to process query, server error", e);
        }
        catch (Exception e)
        {
            log.error("Unable to process query, unknown error: [{}]", query, e);
            throw new SolrException("Unable to process query, unknown error", e);
        }
    }

    /**
     * @param authentication
     *            {@link Authentication} to build Data Access Filters for
     * @param solrQuery
     *            {@link CommonParams#Q} query string (q)
     * @param firstRow
     *            {@link CommonParams#START} beginning row to return results from
     * @param maxRows
     *            {@link CommonParams#ROWS} max number of results to return
     * @param sort
     *            {@code} String representation of {@link SolrQuery.SortClause}
     * @param indent
     *            {@code boolean} true to indent response json
     * @param rowQueryParameters
     *            {@link SolrQuery#setParam(String, String...)} Raw query parameters to add to query
     * @param filterSubscriptionEvents
     *            {@code boolean} should add appropriate subscription filter queries to data access filter
     *            , see {@link SolrDataAccessFilterUtil#process(Authentication, SolrQuery, SolrDataAccessOptions)}
     * @param defaultField
     *            which default filed to be set. Can be null(than default field defined in solrconfig.xml is used)
     * @param isIncludeDACFilter
     *            {@code boolean} whether should add acl filters on solr query
     * @param isIncludeDenyAccessFilter
     *            {@code boolean} whether to include deny access filters in query
     * @param isEnableDocumentACL
     *            {@code boolean} whether to include document ACLs in query
     * @param fields
     *            CSV of fields to request to Solr
     * @return {@link SolrQuery} to send to Solr with {@link SolrClient}
     * @throws SolrException
     *             if unable to build query
     */
    public SolrQuery buildQuery(Authentication authentication, String solrQuery, int firstRow, int maxRows,
            String sort, boolean indent, String rowQueryParameters, boolean filterSubscriptionEvents,
            String defaultField, boolean isIncludeDACFilter, boolean isIncludeDenyAccessFilter, boolean isEnableDocumentACL, String fields)
            throws SolrException
    {
        log.trace("Building query [{}] for user [{}]", solrQuery, authentication.getName());

        SolrQuery query = buildQueryFromUrlParameters(solrQuery);

        query.setRows(maxRows)
                .setStart(firstRow)
                .setParam(SOLR_PARAM_INDENT, indent)
                .setParam(SOLR_PARAM_WRITER, SOLR_WRITER_JSON);

        if (StringUtils.isNotEmpty(sort))
        {
            query.addSort(buildSort(sort));
        }

        if (StringUtils.isNotEmpty(defaultField))
        {
            // Append to existing 'df'
            log.trace("Adding 'df' to query: [{}]", defaultField);
            query.add(SOLR_PARAM_DEFAULT_FIELD, defaultField);
        }

        List<QueryParameter> queryParameters = new ArrayList<>();
        if (StringUtils.isNotEmpty(rowQueryParameters))
        {
            log.trace("rawQueryParameters found, parsing: [{}]", rowQueryParameters);
            queryParameters.addAll(parseRowQueryParameters(rowQueryParameters));
            if (!queryParameters.isEmpty())
            {
                queryParameters.forEach(qp -> query.add(qp.getKey(), qp.getValue()));
            }
        }

        if (StringUtils.isNotEmpty(fields))
        {
            log.trace("Adding [{}] to 'fl' params", fields);
            List<String> parsedFields = parseFields(fields);
            log.trace("Parsed [{}] to add to 'fl' params", parsedFields);
            parsedFields.forEach(query::addField);
        }

        log.trace("Initial query: [{}]", query);

        // Apply DAC
        SolrDataAccessOptions options = new SolrDataAccessOptions();
        options.setFilterSubscriptionEvents(filterSubscriptionEvents);
        options.setIncludeDACFilter(isIncludeDACFilter);
        options.setIncludeDenyAccessFilter(isIncludeDenyAccessFilter);
        options.setEnableDocumentACL(isEnableDocumentACL);
        options.setQueryParameters(queryParameters);

        return SolrDataAccessFilterUtil.process(authentication, options, query);
    }

    private SolrQuery buildQueryFromUrlParameters(String solrQuery)
    {
        SolrQuery query = new SolrQuery();

        String[] params = solrQuery.split("&");

        for (String param : params)
        {
            if (param.startsWith("q="))
            {
                query.setQuery(parse(param.substring("q=".length())));
            }
            else if (param.startsWith("fq="))
            {
                query.addFilterQuery(param.substring("fq=".length()));
            }
            else if (param.equals("facet=true"))
            {
                query.setFacet(true);
            }
            else if (param.startsWith("facet.field="))
            {
                query.addFacetField(param.substring("facet.field=".length()));
            }
            else if (param.startsWith("facet.mincount="))
            {
                query.setFacetMinCount(Integer.parseInt(param.substring("facet.mincount=".length())));
            }
            else if (param.startsWith("fl="))
            {
                query.setFields(param.substring("fl=".length()));
            }
            else if (param.startsWith("indent="))
            {
                // we set indentation according to method parameter.
            }
            else if (param.startsWith("wt="))
            {
                // we set the output to json by default.
            }
            else if (param.startsWith("start="))
            {
                // we set start row according to method parameter.
            }
            else if (param.startsWith("rows="))
            {
                // we set rows according to method parameter.
            }
            else if (param.startsWith("sort="))
            {
                // we set rows according to method parameter.
            }
            else if (param.startsWith("omitHeader="))
            {
                // we set omitHeader to true or false by configuration. Check "solr.omitHeader" configuration
            }
            else
            {
                query.setQuery(parse(param));
            }
        }
        return query;
    }

    private List<String> parseFields(String fields)
    {
        return Arrays.stream(fields.split(",")).filter(StringUtils::isNotEmpty).map(String::trim).collect(Collectors.toList());
    }

    /**
     * Parse and sanitize given {@code solrQuery} before sending on to SolrClient
     *
     * @param solrQuery
     *            {@code String} query to parse and sanitize
     * @return parsed and sanitized (if necessary) solrQuery, or original solrQuery if nothing was changed
     */
    protected String parse(String solrQuery)
    {
        // Try to URL decode solrQuery to maintain backwards compatibility
        if (StringUtils.isNotEmpty(solrQuery))
        {
            try
            {
                // URL Decode any values here, this is to support backwards compatibility
                return URLDecoder.decode(solrQuery, Charset.defaultCharset().displayName());
            }
            catch (UnsupportedEncodingException e)
            {
                log.warn("Unable to decode solrQuery!: [{}]", solrQuery);
            }
        }

        return solrQuery;
    }

    /**
     * Map {@link SolrCore} to appropriate SolrJ handler and/or collection/core name.
     *
     * @param core
     *            {@link SolrCore} to map to collection/core/handler name for SolrJ
     * @param configuration
     *            {@link SolrConfig} to use to configure query
     * @param query
     *            {@link SolrQuery} to modify based on which core/handler specified
     * @return {@code String} name of collection/core for SolrJ to use
     * @throws SolrException
     *             If unable to map {@link SolrCore} to collection/core name
     */
    public String mapCollection(SolrCore core, SolrConfig configuration, SolrQuery query) throws SolrException
    {
        if (core == null)
        {
            throw new SolrException("Invalid collection/core given for SolrRequest");
        }

        return core.configure(configuration, query).getCore(configuration);
    }

    /**
     * Parse {@code rawQueryParameters} for {@link QueryParameter} to add to
     * {@link SolrQuery#setParam(String, String...)}
     * QueryParameters are of the format <code>QUERY_PARAM_FIELD=QUERY_PARAM_VALUE</code>. Ignores empty
     * parameter values (i.e. 'fq=')
     *
     * @param rawQueryParameters
     *            Raw query parameters to add
     * @return {@code List} of parsed {@code rawQueryParameters}
     */
    public List<QueryParameter> parseRowQueryParameters(String rawQueryParameters) throws SolrException
    {
        List<QueryParameter> parameters = new ArrayList<>();
        log.trace("Parsing Query Parameters");
        if (StringUtils.isNotEmpty(rawQueryParameters))
        {
            try
            {
                // First url decode entire parameter string...backwards compatibility
                String decodedRawQueryParameters = URLDecoder.decode(rawQueryParameters, Charset.defaultCharset().displayName());

                List<String> rqps = Arrays.stream(decodedRawQueryParameters.split(SOLR_RAW_QUERY_PARAM_SPLITTER))
                        .filter(StringUtils::isNotEmpty) // Remove errant values
                        .collect(Collectors.toList());
                log.trace("Un-sanitized query parameters found: [{}]", rqps);

                for (String value : rqps)
                {
                    log.trace("Parsing...[{}]", value);

                    // Only consider the first '=' to be the separator
                    String[] qpValues = value.split(SOLR_RAW_QUERY_SPLITTER, 2);

                    // ignore empty parameter values (i.e. 'fq=')
                    if (qpValues.length < 2)
                    {
                        log.trace("Parsing...found empty parameter value for: [{}]", value);
                        continue;
                    }

                    if (qpValues.length == 2)
                    {
                        QueryParameter qp = new QueryParameter();
                        String qpParamField = qpValues[0];
                        String qpParamValue = qpValues[1];

                        qp.setKey(qpParamField);
                        qp.setValue(qpParamValue);
                        parameters.add(qp);
                    }
                    else
                    {
                        log.error("Found invalid raw query parameter, something went wrong with parsing?: [{}]", value);
                        throw new SolrException("Found invalid raw query parameter");
                    }
                }
            }
            catch (UnsupportedEncodingException e)
            {
                log.warn("Unable to decode rawQueryParameters!: [{}]", rawQueryParameters);
                throw new SolrException("Unable to decode raw query parameter!");
            }

            log.trace("Query Parameters parsed: [{}]", parameters);
        }
        else
        {
            log.trace("No Query Parameters found");
        }

        return parameters;
    }

    /**
     * Build SolrJ sort parameter from ArkCase sort parameter
     * Note: The order part of sort string will be lowercased, see {@link org.apache.solr.client.solrj.SolrQuery.ORDER}
     *
     * @param sort
     *            {@code String} to parse
     * @return {@link org.apache.solr.client.solrj.SolrQuery.SortClause} SortClause from parsed {@code sort}
     * @throws SolrException
     */
    public SolrQuery.SortClause buildSort(String sort) throws SolrException
    {
        log.trace("Parsing sort from: [{}]", sort);
        String[] fieldAndOrder = sort.split(SOLR_SORT_CLAUSE_SPLITTER);
        if (fieldAndOrder.length != 2)
        {
            log.error("Invalid sort parameter found: [{}]", sort);
            throw new SolrException("Invalid sort parameter");
        }
        try
        {
            SolrQuery.SortClause sortClause = new SolrQuery.SortClause(fieldAndOrder[0], fieldAndOrder[1].toLowerCase());
            log.trace("Sort clause parsed: [{}]", sortClause);
            return sortClause;
        }
        catch (Exception e)
        {
            log.error("Unable to build sort clause: [{}]", sort);
            throw new SolrException("Unable to build sort clause");
        }
    }

    /**
     * This method executes delete of documents in solr using search query
     * (Beware) it could easily delete all documents if query is not specific
     *
     * @param queueName
     *            Which queue to send the request to
     * @param query
     *            Solr Search query for removing found documents
     * @throws SolrException
     */
    public void sendSolrDeleteQuery(String queueName, String query) throws SolrException
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

    public SolrClientProvider getSolrClientProvider()
    {
        return solrClientProvider;
    }

    public void setSolrClientProvider(SolrClientProvider solrClientProvider)
    {
        this.solrClientProvider = solrClientProvider;
    }

    public SolrConfig getConfiguration()
    {
        return configuration;
    }

    public void setConfiguration(SolrConfig configuration)
    {
        this.configuration = configuration;
    }
}
