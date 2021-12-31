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

import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_REF_S;

import com.armedia.acm.audit.model.AuditEventConfig;
import com.armedia.acm.services.search.exception.SolrException;
import com.armedia.acm.services.search.model.SearchConstants;
import com.armedia.acm.services.search.model.solr.SearchConfig;
import com.armedia.acm.services.search.model.solr.SolrCore;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.security.core.Authentication;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dmiller on 2/23/16.
 */
public class FacetedSearchService
{
    private transient final Logger log = LogManager.getLogger(this.getClass());
    private SearchConfig searchConfig;
    private AuditEventConfig auditEventConfig;
    private ExecuteSolrQuery executeSolrQuery;

    /**
     * Pattern for matching words separated by white space, and also words which are quoted containing whitespace.
     * Example: 'Lorem ipsum dolor sit "amet sollicitudin" id' -> Lorem, ipsum, dolor, sit, "amet sollicitudin", id
     */
    private Pattern termsPattern = Pattern.compile("([^\"]\\S*|\".+?\")\\s*");

    public String buildHiddenDocumentsFilter()
    {
        return " " + SearchConstants.OPERATOR_AND + " hidden_b:false";
    }

    /**
     * Convert the facet specifications in searchPlugin.properties into Solr facet query parameters.
     * <p>
     * The searchPlugin.properties must contain a property "search.time.period" which has a list of Solr time
     * specifications, like this:
     * <p>
     *
     * <pre>
     *     ## Time periods used by the faceted search
     * search.time.period=[{"desc": "Previous Week", "value":"[NOW/DAY-7DAY TO *]"} \
     * ,{"desc": "Previous Month", "value":"[NOW/DAY-1MONTH TO *]"}   \
     * ,{"desc": "Previous Year", "value":"[NOW/DAY-1YEAR TO *]"} \
     * ]
     * </pre>
     * <p>
     * Note, these time periods are stored as a JSON array, where each object in the array contains a description and a
     * value. The value must be a valid Solr time interval (see examples above).
     * </p>
     * <p>
     * The searchPlugin.properties should also have a list of facet specifications, like this:
     * </p>
     *
     * <pre>
     * facet.date.modified_date_tdt=Modify Date
     * facet.modifier_lcs=Modify User
     * </pre>
     * <p>
     * For each property that starts with "facet.date", this method will return one query parameter for each object in
     * the search.time.period JSON array. Based on the above example search.time.period, the return value includes query
     * parameters for "Previous Week", "Previous Month" and "Previous Year" for the Solr field "modified_date_tdt".
     * </p>
     * <p>
     * For each property that starts with "facet" (but does not start with "facet.date") this method returns one query
     * parameter: the facet.field specification for that property.
     * </p>
     * <p>
     * The return value will be sent to Solr as part of the faceted search query.
     * </p>
     *
     * @return Facet field specifications suitable for sending to Solr. Example:
     *         <p>
     *         facet.query="{!key='Birth Date, Previous Week'}birth_date_tdt:[NOW/DAY-7DAY TO
     *         *]&facet.field={!key='First Name'}first_name_s&facet.field={!key='Last Name'}last_name_s
     */
    public String getFacetKeys()
    {
        List<String> facetKeys = new ArrayList<>();

        String jsonString = searchConfig.getTimePeriod();
        JSONArray timePeriodList = new JSONArray(jsonString);
        JSONObject timePeriodJSONObject;

        try
        {
            for (Map.Entry<String, String> e : searchConfig.getFacets().entrySet())
            {
                String facetKey = e.getKey();
                {
                    if (facetKey.contains(SearchConstants.DATE_FACET_PRE_KEY))
                    {
                        facetKey = facetKey.replaceFirst(SearchConstants.DATE_FACET_PRE_KEY, "");
                        for (int i = 0; i < timePeriodList.length(); i++)
                        {
                            timePeriodJSONObject = timePeriodList.getJSONObject(i);

                            String timePeriod = URLEncoder.encode(
                                    "{" + SearchConstants.SOLR_FACET_NAME_CHANGE_COMMAND + "'" + e.getValue() + ", "
                                            + timePeriodJSONObject.getString(SearchConstants.TIME_PERIOD_DESCRIPTION) + "'}",
                                    SearchConstants.FACETED_SEARCH_ENCODING);
                            String timePeriodValue = URLEncoder.encode(timePeriodJSONObject.getString(SearchConstants.TIME_PERIOD_VALUE),
                                    SearchConstants.FACETED_SEARCH_ENCODING);
                            facetKeys.add(
                                    SearchConstants.FACET_QUERY + timePeriod + facetKey + SearchConstants.DOTS_SPLITTER + timePeriodValue);

                        }
                    }
                    else
                    {
                        String encoded = URLEncoder.encode("{" + SearchConstants.SOLR_FACET_NAME_CHANGE_COMMAND + "'" + e.getValue() + "'}",
                                SearchConstants.FACETED_SEARCH_ENCODING);
                        facetKeys.add(SearchConstants.FACET_FILED + encoded + facetKey);

                    }
                }
            }
        }
        catch (UnsupportedEncodingException e1)
        {

            log.error("Encoding problem while building the facet key list: " + e1.getMessage(), e1);
        }

        return String.join(SearchConstants.AND_SPLITTER, facetKeys);
    }

    /**
     * Convert a filter into a Solr-compatible search query parameter.
     * <p>
     * If the filter is based on a facet name ("Last Name":Garcia), then the method looks up the corresponding facet
     * property from searchPlugin.properties, and returns a proper Solr field search: fq={!field f=last_name_s}Garcia.
     * </p>
     * <p>
     * If the filter is a Solr field name (not a facet name), like this: object_id_s:1401, then the return value is just
     * the equivalent filter query: fq=object_id_s:1401.
     * </p>
     * <p>
     * In any case the return value is suitable for sending to Solr. Each such filter will restrict the search results
     * to only those records that meet the filter query.
     * </p>
     *
     * @param filter
     *            A filter term; it can use either a facet name ("Last Name":Garcia), or a Solr field name
     *            (object_id_s:1401). If the term uses a facet name, the searchPlugin.properties must have a
     *            corresponding property starting with "facet.", i.e., facet.last_name_s=Last Name.
     * @return A suitable Solr filter query clause that can be sent to Solr as is.
     */
    public String buildSolrQuery(String filter)
    {
        if (!StringUtils.isBlank(filter))
        {
            try
            {
                filter = URLDecoder.decode(filter, SearchConstants.FACETED_SEARCH_ENCODING);
                String solrFiltersSubQuery = createFacetedFiltersSubString(filter);
                return solrFiltersSubQuery;

            }
            catch (UnsupportedEncodingException e)
            {
                log.error("Encoding problem occur while building SOLR query for faceted search with filters: " + filter);
            }
        }

        return filter;
    }

    /**
     * Take the given query, and add a clause onto that query, such that the query excludes object types that should not
     * be included in the search results. Most searches should not include notifications or subscriptions, each of which
     * have their own special modules.
     * <p>
     * The searchPlugin.properties must include a property named objects.to.exclude, like this:
     *
     * <pre>
     *     objects.to.exclude=NOTIFICATION,SUBSCRIPTION_EVENT
     * </pre>
     * </p>
     * <p>
     * If this property is empty, no object types will be excluded.
     * </p>
     * <p>
     * <p>
     * Note, if the rowQueryParameters parameter indicates the search particularly wants to include an excluded type,
     * then that type will not be excluded after all. This allows a user to search precisely for an object type that
     * would otherwise be excluded.
     * </p>
     *
     * @param query
     *            The query to be updated so as to exclude object types that should not appear.
     * @param rowQueryParameters
     *            Filters for the given query; if the filters specifically include a type in the objects.to.exclude
     *            property, then that object type will not be excluded after all.
     * @return Updated query string to exclude any excluded types that are not specifically included by the query.
     */
    public String updateQueryWithExcludedObjects(String query, String rowQueryParameters)
    {
        if (query != null)
        {
            String[] objectsToExcludeArray = getObjectsToExclude();

            String subQuery = getObjectsToExcludeSubQuery(objectsToExcludeArray, rowQueryParameters);

            if (!"".equals(subQuery))
            {
                query += " " + SearchConstants.OPERATOR_AND + " " + subQuery;
            }
        }

        return query;
    }

    /**
     * Post-process the Solr search results by replacing occurrences of event type keys with the corresponding event
     * type name. Event types are defined in the eventType.properties file like so:
     * <p>
     *
     * <pre>
     * eventType.com.armedia.acm.widget.created=New Widget Created
     * </pre>
     * </p>
     * <p>
     * Given the above property, every occcurrence of "com.armedia.acm.widget.created" anywhere in the search results
     * will be replaced by "New Widget Created".
     * </p>
     *
     * @param solrResult
     *            Search results from Solr.
     * @return The same search results, with event type keys replaced with event type names, as described above.
     */
    public String replaceEventTypeName(String solrResult)
    {
        for (Map.Entry<String, String> e : auditEventConfig.getEventTypes().entrySet())
        {
            String key;
            if (e.getKey().contains(SearchConstants.EVENT_TYPE))
            {
                key = e.getKey().replaceFirst(SearchConstants.EVENT_TYPE, "");
                if (solrResult.contains("\"" + key.trim() + "\""))
                {
                    solrResult = solrResult.replaceAll("\"" + key + "\"", "\"" + e.getValue() + "\"");
                }
            }

        }
        return solrResult;
    }

    /**
     * Same as ClentUtils.escapeChars, except c == '*' and Character.isWhitespace(c) is removed
     *
     * @param s
     *            String to have Solr's special characters escaped
     * @return escaped string
     */
    public String escapeQueryChars(String s)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            // These characters are part of the query syntax and must be escaped
            if (c == '\\'
                    || c == '+'
                    || c == '-'
                    || c == '!'
                    || c == '('
                    || c == ')'
                    || c == ':'
                    || c == '^'
                    || c == '['
                    || c == ']'
                    || c == '\"'
                    || c == '{'
                    || c == '}'
                    || c == '~'
                    || c == '?'
                    || c == '|'
                    || c == '&'
                    || c == ';'
                    || c == '/')
            {
                sb.append('\\');
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public String createFacetedFiltersSubString(String filter) throws UnsupportedEncodingException
    {
        StringBuilder queryBuilder = new StringBuilder();
        String timePeriods = searchConfig.getTimePeriod();
        JSONArray timePeriodList = new JSONArray(timePeriods);
        if (filter.contains(SearchConstants.AND_SPLITTER))
        {
            String[] fqs = filter.split(SearchConstants.AND_SPLITTER);
            for (String name : fqs)
            {
                String[] filterSplitByQ = name.split(SearchConstants.QUOTE_SPLITTER);
                String[] filterSplitByDots = name.split(SearchConstants.DOTS_SPLITTER);
                // if the search key is not quoted, take everything before the colon
                String searchKey = filterSplitByQ.length > 1 ? filterSplitByQ[1] : filterSplitByDots[0];
                if (filterSplitByDots.length > 1)
                {
                    buildFacetFilter(queryBuilder, timePeriodList, searchKey, filterSplitByDots[1]);
                }
                else
                {
                    // if search key is unset, use empty value.
                    buildFacetFilter(queryBuilder, timePeriodList, searchKey, "");
                }
            }
        }
        else
        {
            String[] filterSplitByQ = filter.split(SearchConstants.QUOTE_SPLITTER);
            String[] filterSplitByDots = filter.split(SearchConstants.DOTS_SPLITTER);
            // if the search key is not quoted, take everything before the colon
            String searchKey = filterSplitByQ.length > 1 ? filterSplitByQ[1] : filterSplitByDots[0];
            if (filterSplitByDots.length > 1)
            {
                buildFacetFilter(queryBuilder, timePeriodList, searchKey, filterSplitByDots[1]);
            }
            else
            {
                // if search key is unset, use empty value.
                buildFacetFilter(queryBuilder, timePeriodList, searchKey, "");
            }
        }
        return queryBuilder.toString();
    }

    protected void buildFacetFilter(StringBuilder queryBuilder, JSONArray jsonArray, String searchKey,
                                    String filterSplitByDot) throws UnsupportedEncodingException
    {
        String[] allORFilters = filterSplitByDot.contains("|") ? filterSplitByDot.split(SearchConstants.PIPE_SPLITTER) : null;

        boolean isFacetFilter = false;

        for (Map.Entry<String, String> mapElement : searchConfig.getFacets().entrySet())
        {
            if (searchKey.equals(mapElement.getValue()))
            {
                isFacetFilter = true;

                if (mapElement.getKey().contains(SearchConstants.DATE_FACET_PRE_KEY))
                {
                    if (allORFilters == null)
                    {
                        String returnedDateANDSubString = createDateANDQuerySubString(jsonArray, mapElement.getKey(),
                                filterSplitByDot.trim());
                        queryBuilder.append(returnedDateANDSubString);
                        break;
                    }
                    else
                    {
                        String returnedDateORSubString = createDateORQuerySubString(allORFilters, jsonArray, mapElement.getKey());
                        queryBuilder.append(returnedDateORSubString);
                        break;
                    }
                }
                else
                {
                    if (allORFilters == null)
                    {
                        String returnedRegularANDSubString = createRegularANDQuerySubString(mapElement.getKey(), filterSplitByDot.trim());
                        queryBuilder.append(returnedRegularANDSubString);
                        break;
                    }
                    else
                    {
                        String returnedRegularORSubString = createRegularORQuerySubString(allORFilters, mapElement.getKey());
                        queryBuilder.append(returnedRegularORSubString);
                        break;

                    }
                }
            }
        }

        if (!isFacetFilter)
        {
            queryBuilder.append(SearchConstants.SOLR_FILTER_QUERY_ATTRIBUTE_NAME).append(searchKey).append(SearchConstants.DOTS_SPLITTER)
                    .append(URLEncoder.encode(filterSplitByDot.trim(), SearchConstants.FACETED_SEARCH_ENCODING));
        }
    }

    private String createDateORQuerySubString(String[] allORFilters, JSONArray jsonArrayOfDatePeriods, String filterKey)
    {
        String substitutionName = filterKey.replaceFirst(SearchConstants.DATE_FACET_PRE_KEY, "");
        String value = null;
        StringBuilder query = new StringBuilder(SearchConstants.SOLR_FILTER_QUERY_ATTRIBUTE_NAME);
        boolean isFirst = true;
        try
        {
            for (String orFilter : allORFilters)
            {
                for (int i = 0; i < jsonArrayOfDatePeriods.length(); i++)
                {
                    if (jsonArrayOfDatePeriods.getJSONObject(i).getString(SearchConstants.TIME_PERIOD_DESCRIPTION).equals(orFilter.trim()))
                    {
                        value = jsonArrayOfDatePeriods.getJSONObject(i).getString(SearchConstants.TIME_PERIOD_VALUE);
                        break;
                    }
                }
                if (isFirst)
                {
                    isFirst = false;
                    query.append(
                            URLEncoder.encode(substitutionName + SearchConstants.DOTS_SPLITTER, SearchConstants.FACETED_SEARCH_ENCODING))
                            .append(URLEncoder.encode("(" + value, SearchConstants.FACETED_SEARCH_ENCODING));
                }
                else
                {
                    query.append(URLEncoder.encode(" OR ", SearchConstants.FACETED_SEARCH_ENCODING))
                            .append(URLEncoder.encode(value, SearchConstants.FACETED_SEARCH_ENCODING));
                }
            }
            query.append(URLEncoder.encode(")", SearchConstants.FACETED_SEARCH_ENCODING));
        }
        catch (UnsupportedEncodingException e1)
        {
            log.error("Encoding problem occur while building date OR SOLR query sub-string", e1);
        }
        return query.toString();
    }

    private String createRegularORQuerySubString(String[] allORFilters, String filterKey)
    {
        StringBuilder query = new StringBuilder(SearchConstants.SOLR_FILTER_QUERY_ATTRIBUTE_NAME);
        boolean isFirst = true;
        for (String orFilter : allORFilters)
        {
            try
            {
                if (isFirst)
                {
                    isFirst = false;
                    // AFDP-1101 The term query parser is not what we want here; we want a field search. so use the
                    // field query parser.
                    query.append(
                            URLEncoder.encode("_query_:\"{!field f=" + filterKey + "}", SearchConstants.FACETED_SEARCH_ENCODING))
                            .append(URLEncoder.encode(orFilter.trim() + "\"", SearchConstants.FACETED_SEARCH_ENCODING));
                }
                else
                {
                    query.append(
                            URLEncoder.encode(" OR _query_:\"{!field f=" + filterKey + "}", SearchConstants.FACETED_SEARCH_ENCODING))
                            .append(URLEncoder.encode(orFilter.trim() + "\"", SearchConstants.FACETED_SEARCH_ENCODING));
                }
            }
            catch (UnsupportedEncodingException e1)
            {
                log.error("Encoding problem occur while building regular OR SOLR query sub-string", e1);
            }
        }
        return query.toString();
    }

    private String createRegularANDQuerySubString(String filterKey, String filterValue)
    {

        String query = SearchConstants.SOLR_FILTER_QUERY_ATTRIBUTE_NAME;
        try
        {
            // AFDP-1101 The term query parser is not what we want here; we want a field search. so use the field query
            // parser.
            query += URLEncoder.encode("{!field f=" + filterKey + "}", SearchConstants.FACETED_SEARCH_ENCODING)
                    + URLEncoder.encode(filterValue, SearchConstants.FACETED_SEARCH_ENCODING);
        }
        catch (UnsupportedEncodingException e)
        {
            log.error("Encoding problem occur while building regular AND SOLR query sub-string", e);
        }
        return query;
    }

    private String createDateANDQuerySubString(JSONArray jsonArray, String filterKey, String filterValue)
    {
        String query = SearchConstants.SOLR_FILTER_QUERY_ATTRIBUTE_NAME;
        String substitutionName = filterKey.replaceFirst(SearchConstants.DATE_FACET_PRE_KEY, "");
        String value = null;
        for (int i = 0; i < jsonArray.length(); i++)
        {
            if (jsonArray.getJSONObject(i).getString(SearchConstants.TIME_PERIOD_DESCRIPTION).equals(filterValue.trim()))
            {
                value = jsonArray.getJSONObject(i).getString(SearchConstants.TIME_PERIOD_VALUE);
            }
        }
        try
        {
            query += URLEncoder.encode(substitutionName + SearchConstants.DOTS_SPLITTER, SearchConstants.FACETED_SEARCH_ENCODING)
                    + URLEncoder.encode(value, SearchConstants.FACETED_SEARCH_ENCODING);
        }
        catch (UnsupportedEncodingException e)
        {
            log.error("Encoding problem occur while building date AND SOLR query sub-string", e);
        }
        return query;
    }

    private String[] getObjectsToExclude()
    {

        String objectsToExclude = searchConfig.getObjectsToExclude();

        if (objectsToExclude != null && !"".equals(objectsToExclude))
        {
            return objectsToExclude.split(",");
        }

        return null;
    }

    private String getObjectsToExcludeSubQuery(String[] objectsToExcludeArray, String queryParameters)
    {
        String subQuery = "";

        if (objectsToExcludeArray != null)
        {

            // AFDP-1101: the filter line says, "if the user specifically requested a certain object type, then do not
            // exclude it after all". We have to check for the URL-encoded search term... Value sent to SOLR is like:
            // {!field f=object_type_facet}NOTIFICATION - meaning to include objects of type NOTIFICATION. The
            // URL-encoded version of this search term is "%21field+f%3Dobject_type_facet%7DNOTIFICATION"... so that's
            // what we exclude from the results of this stream.
            subQuery = Arrays.stream(objectsToExcludeArray)
                    .filter((String element) -> !queryParameters
                            .contains("%21field+f%3D" + SearchConstants.PROPERTY_OBJECT_TYPE_FACET + "%7D" + element) &&
                            !queryParameters.contains("%21field+f%3D" + SearchConstants.PROPERTY_OBJECT_TYPE + "%7D" + element))
                    .map((String element) -> "-" + SearchConstants.PROPERTY_OBJECT_TYPE + ":" + element)
                    .reduce((String left, String right) -> left + " " + SearchConstants.OPERATOR_AND + " " + right)
                    .orElse("");
        }

        return subQuery;
    }

    /**
     *
     * splits by whitespace search terms and escapes each term with double quotes.
     * If term is already escaped with double quotes, it's ignored
     *
     * @param query
     *            raw query
     * @return String with escaped search terms
     */
    public String escapeTermsInQuery(String query)
    {
        if (StringUtils.isEmpty(query))
        {
            return "";
        }
        query = query.trim();
        StringBuilder sb = new StringBuilder();
        for (String term : getSearchTerms(query))
        {
            if (term.endsWith("\"") && term.startsWith("\""))
            {
                // already escaped with quotes, don't escape it
                sb.append(" ").append(term);
            }
            else
            {
                // it must be escaped because term can contain special character which can interfere constructing the
                // query
                sb.append(" \"").append(term).append("\"");
            }
        }
        return sb.toString().trim();
    }

    private List<String> getSearchTerms(String searchString)
    {
        Matcher m = termsPattern.matcher(searchString);
        List<String> terms = new LinkedList<>();
        while (m.find())
        {
            terms.add(m.group(1));
        }
        return terms;
    }

    public SearchConfig getSearchConfig()
    {
        return searchConfig;
    }

    public void setSearchConfig(SearchConfig searchConfig)
    {
        this.searchConfig = searchConfig;
    }

    public AuditEventConfig getAuditEventConfig()
    {
        return auditEventConfig;
    }

    public void setAuditEventConfig(AuditEventConfig auditEventConfig)
    {
        this.auditEventConfig = auditEventConfig;
    }

    public JSONObject getParentDocumentJsonObject(Authentication authentication, String res) throws SolrException
    {
        JSONObject solrResponse = new JSONObject(res);
        if (solrResponse.getJSONObject("response").getLong("numFound") > 0)
        {
            JSONArray docs = solrResponse.getJSONObject("response").getJSONArray("docs");
            for (int i = 0; i < docs.length(); i++)
            {
                String parentId = "";
                String parentType = "";
                if (docs.getJSONObject(i).has(PARENT_REF_S))
                {
                    String parentReference = docs.getJSONObject(i).getString(PARENT_REF_S);
                    parentId = StringUtils.substringBefore(parentReference, "-");
                    parentType = StringUtils.substringAfter(parentReference, "-");
                }

                if (StringUtils.isNotEmpty(parentId) && StringUtils.isNotEmpty(parentType))
                {
                    String parentResult = getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.ADVANCED_SEARCH,
                            "object_id_s:" + parentId + " AND object_type_s:" + parentType, 0,
                            1, "create_date_tdt DESC");
                    JSONObject parentResponse = new JSONObject(parentResult);
                    if (parentResponse.getJSONObject("response").getLong("numFound") == 1)
                    {
                        docs.getJSONObject(i).put("parent_document",
                                parentResponse.getJSONObject("response").getJSONArray("docs").getJSONObject(0));
                    }
                }
            }
        }
        return solrResponse;
    }

    public ExecuteSolrQuery getExecuteSolrQuery()
    {
        return executeSolrQuery;
    }

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery)
    {
        this.executeSolrQuery = executeSolrQuery;
    }
}
