package com.armedia.acm.services.search.service;

import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.services.search.model.SearchConstants;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by dmiller on 2/23/16.
 */
public class FacetedSearchService
{
    private AcmPlugin pluginSearch;
    private AcmPlugin pluginEventType;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    public String buildHiddenDocumentsFilter()
    {
        return " " + SearchConstants.OPERATOR_AND + " hidden_b:false";
    }

    public String getFacetKeys()
    {
        StringBuilder queryBuilder = new StringBuilder();
        Map<String, Object> propertyMap = getPluginSearch().getPluginProperties();
        String jsonString = (String) propertyMap.get(SearchConstants.TIME_PERIOD_KEY);
        JSONArray jsonArray = new JSONArray(jsonString);
        JSONObject timePeriodJSONObject;
        for (Map.Entry<String, Object> e : propertyMap.entrySet())
        {
            if (e.getKey().contains(SearchConstants.FACET_PRE_KEY))
            {
                String facetKey = e.getKey().replaceFirst(SearchConstants.FACET_PRE_KEY, "");
                if (e.getKey().contains(SearchConstants.DATE_FACET_PRE_KEY))
                {
                    facetKey = e.getKey().replaceFirst(SearchConstants.DATE_FACET_PRE_KEY, "");
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        timePeriodJSONObject = jsonArray.getJSONObject(i);
                        try
                        {
                            String timePeriod = URLEncoder.encode(
                                    "{" + SearchConstants.SOLR_FACET_NAME_CHANGE_COMMAND + "'" + e.getValue() + ", " +
                                            timePeriodJSONObject.getString(SearchConstants.TIME_PERIOD_DESCRIPTION) + "'}", SearchConstants.FACETED_SEARCH_ENCODING);
                            String timePeriodValue = URLEncoder.encode(timePeriodJSONObject.getString(SearchConstants.TIME_PERIOD_VALUE), SearchConstants.FACETED_SEARCH_ENCODING);
                            if (queryBuilder.length() > 0)
                            {
                                queryBuilder.append(SearchConstants.FACET_QUERY_WITH_AND_AS_A_PREFIX + timePeriod +
                                        facetKey + SearchConstants.DOTS_SPLITTER + timePeriodValue);
                            } else
                            {
                                queryBuilder.append(SearchConstants.FACET_QUERY + timePeriod + facetKey + SearchConstants.DOTS_SPLITTER + timePeriodValue);
                            }
                        } catch (UnsupportedEncodingException e1)
                        {

                            log.error("Encoding problem occur while building SOLR query for faceted search with key: " + facetKey, e1);
                        }

                    }
                } else
                {
                    String encoded = null;
                    try
                    {
                        encoded = URLEncoder.encode("{" + SearchConstants.SOLR_FACET_NAME_CHANGE_COMMAND + "'" + e.getValue() + "'}", SearchConstants.FACETED_SEARCH_ENCODING);
                        if (queryBuilder.length() > 0)
                        {
                            queryBuilder.append(SearchConstants.FACET_FILED_WITH_AND_AS_A_PREFIX + encoded + facetKey);
                        } else
                        {
                            queryBuilder.append(SearchConstants.FACET_FILED).append(encoded).append(facetKey);
                        }
                    } catch (UnsupportedEncodingException e1)
                    {
                        log.error("Encoding problem occur while building SOLR query for faceted search with key: " + facetKey, e1);
                    }

                }
            }
        }

        return queryBuilder.toString();
    }

    public String buildSolrQuery(String filters)
    {
        if (!StringUtils.isBlank(filters))
        {
            try
            {
                filters = URLDecoder.decode(filters, SearchConstants.FACETED_SEARCH_ENCODING);
                String solrFiltersSubQuery = createFacetedFiltersSubString(filters);
                return solrFiltersSubQuery;

            } catch (UnsupportedEncodingException e)
            {
                log.error("Encoding problem occur while building SOLR query for faceted search with filters: " + filters);
            }
        }

        return filters;
    }

    private String createFacetedFiltersSubString(String filters) throws UnsupportedEncodingException
    {
        StringBuilder queryBuilder = new StringBuilder();
        Map<String, Object> propertyMap = getPluginSearch().getPluginProperties();
        String jsonString = (String) propertyMap.get(SearchConstants.TIME_PERIOD_KEY);
        JSONArray jsonArray = new JSONArray(jsonString);

        if (filters.contains(SearchConstants.AND_SPLITTER))
        {
            String[] fqs = filters.split(SearchConstants.AND_SPLITTER);
            for (String name : fqs)
            {
                String[] filterSplitByQ = name.split(SearchConstants.QUOTE_SPLITTER);
                String[] filterSplitByDots = name.split(SearchConstants.DOTS_SPLITTER);
                // if the search key is not quoted, take everything before the colon
                String searchKey = filterSplitByQ.length > 1 ? filterSplitByQ[1] : filterSplitByDots[0];
                buildFacetFilter(queryBuilder, propertyMap, jsonArray, searchKey, filterSplitByDots[1]);
            }
        } else
        {
            String[] filterSplitByQ = filters.split(SearchConstants.QUOTE_SPLITTER);
            String[] filterSplitByDots = filters.split(SearchConstants.DOTS_SPLITTER);
            // if the search key is not quoted, take everything before the colon
            String searchKey = filterSplitByQ.length > 1 ? filterSplitByQ[1] : filterSplitByDots[0];
            buildFacetFilter(queryBuilder, propertyMap, jsonArray, searchKey, filterSplitByDots[1]);
        }
        return queryBuilder.toString();
    }

    private void buildFacetFilter(StringBuilder queryBuilder, Map<String, Object> propertyMap, JSONArray jsonArray, String searchKey, String filterSplitByDot)
            throws UnsupportedEncodingException
    {
        String[] allORFilters = filterSplitByDot.contains("|") ? filterSplitByDot.split(SearchConstants.PIPE_SPLITTER) : null;

        boolean isFacetFilter = false;

        for (Map.Entry<String, Object> mapElement : propertyMap.entrySet())
        {
            if (searchKey.equals(mapElement.getValue()) && mapElement.getKey().contains(SearchConstants.FACET_PRE_KEY))
            {
                isFacetFilter = true;

                if (mapElement.getKey().contains(SearchConstants.DATE_FACET_PRE_KEY))
                {
                    if (allORFilters == null)
                    {
                        String returnedDateANDSubString = createDateANDQuerySubString(jsonArray, mapElement.getKey(), filterSplitByDot.trim());
                        queryBuilder.append(returnedDateANDSubString);
                        break;
                    } else
                    {
                        String returnedDateORSubString = createDateORQuerySubString(allORFilters, jsonArray, mapElement.getKey());
                        queryBuilder.append(returnedDateORSubString);
                        break;
                    }
                } else
                {
                    if (allORFilters == null)
                    {
                        String returnedRegularANDSubString = createRegularANDQuerySubString(mapElement.getKey(), filterSplitByDot.trim());
                        queryBuilder.append(returnedRegularANDSubString);
                        break;
                    } else
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
            queryBuilder.append(SearchConstants.SOLR_FILTER_QUERY_ATTRIBUTE_NAME).append(searchKey).append(SearchConstants.DOTS_SPLITTER).append(URLEncoder.encode(filterSplitByDot.trim(), SearchConstants.FACETED_SEARCH_ENCODING));
        }
    }

    private String createDateORQuerySubString(String[] allORFilters, JSONArray jsonArrayOfDatePeriods, String filterKey)
    {
        String substitutionName = filterKey.replaceFirst(SearchConstants.DATE_FACET_PRE_KEY, "");
        String value = null;
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(SearchConstants.SOLR_FILTER_QUERY_ATTRIBUTE_NAME);
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
                    queryBuilder.append(URLEncoder.encode(substitutionName + SearchConstants.DOTS_SPLITTER, SearchConstants.FACETED_SEARCH_ENCODING)).append(URLEncoder.encode("(" + value, SearchConstants.FACETED_SEARCH_ENCODING));
                } else
                {
                    queryBuilder.append(URLEncoder.encode(" OR ", SearchConstants.FACETED_SEARCH_ENCODING) + URLEncoder.encode(value, SearchConstants.FACETED_SEARCH_ENCODING));
                }
            }
            queryBuilder.append(URLEncoder.encode(")", SearchConstants.FACETED_SEARCH_ENCODING));
        } catch (UnsupportedEncodingException e1)
        {
            log.error("Encoding problem occur while building date OR SOLR query sub-string", e1);
        }
        return queryBuilder.toString();
    }

    private String createRegularORQuerySubString(String[] allORFilters, String filterKey)
    {
        String substitutionName = filterKey.replaceFirst(SearchConstants.FACET_PRE_KEY, "");
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(SearchConstants.SOLR_FILTER_QUERY_ATTRIBUTE_NAME);
        boolean isFirst = true;
        for (String orFilter : allORFilters)
        {
            try
            {
                if (isFirst)
                {
                    isFirst = false;
                    // AFDP-1101 The term query parser is not what we want here; we want a field search. so use the field query parser.
                    queryBuilder.append(URLEncoder.encode("_query_:\"{!field f=" + substitutionName + "}", SearchConstants.FACETED_SEARCH_ENCODING)).append(URLEncoder.encode(orFilter.trim() + "\"", SearchConstants.FACETED_SEARCH_ENCODING));
                } else
                {
                    queryBuilder.append(URLEncoder.encode(" OR _query_:\"{!field f=" + substitutionName + "}", SearchConstants.FACETED_SEARCH_ENCODING));
                    queryBuilder.append(URLEncoder.encode(orFilter.trim() + "\"", SearchConstants.FACETED_SEARCH_ENCODING));
                }
            } catch (UnsupportedEncodingException e1)
            {
                if (log.isErrorEnabled())
                {
                    log.error("Encoding problem occur while building regular OR SOLR query sub-string", e1);
                }
            }
        }
        return queryBuilder.toString();
    }

    private String createRegularANDQuerySubString(String filterKey, String filterValue)
    {
        StringBuilder queryBuilder = new StringBuilder();
        String substitutionName = filterKey.replaceFirst(SearchConstants.FACET_PRE_KEY, "");
        try
        {
            // AFDP-1101 The term query parser is not what we want here; we want a field search. so use the field query parser.
            queryBuilder.append(SearchConstants.SOLR_FILTER_QUERY_ATTRIBUTE_NAME);
            queryBuilder.append(URLEncoder.encode("{!field f=" + substitutionName + "}", SearchConstants.FACETED_SEARCH_ENCODING));
            queryBuilder.append(URLEncoder.encode(filterValue, SearchConstants.FACETED_SEARCH_ENCODING));
        } catch (UnsupportedEncodingException e)
        {
            if (log.isErrorEnabled())
            {
                log.error("Encoding problem occur while building regular AND SOLR query sub-string", e);
            }
        }
        return queryBuilder.toString();
    }

    private String createDateANDQuerySubString(JSONArray jsonArray, String filterKey, String filterValue)
    {
        StringBuilder queryBuilder = new StringBuilder();
        String substitutionName = filterKey.replaceFirst(SearchConstants.DATE_FACET_PRE_KEY, "");
        String value = null;
        for (int i = 0; i < jsonArray.length(); i++)
        {
            if (jsonArray.getJSONObject(i).getString(SearchConstants.TIME_PERIOD_DESCRIPTION).equals(filterValue.trim()))
                value = jsonArray.getJSONObject(i).getString(SearchConstants.TIME_PERIOD_VALUE);
        }
        try
        {
            queryBuilder.append(SearchConstants.SOLR_FILTER_QUERY_ATTRIBUTE_NAME);
            queryBuilder.append(URLEncoder.encode(substitutionName + SearchConstants.DOTS_SPLITTER, SearchConstants.FACETED_SEARCH_ENCODING));
            queryBuilder.append(URLEncoder.encode(value, SearchConstants.FACETED_SEARCH_ENCODING));
        } catch (UnsupportedEncodingException e)
        {
            if (log.isErrorEnabled())
            {
                log.error("Encoding problem occur while building date AND SOLR query sub-string", e);
            }
        }
        return queryBuilder.toString();
    }

    public String updateQueryWithExcludedObjects(String query, String rowQueryParametars)
    {
        if (query != null)
        {
            String[] objectsToExcludeArray = getObjectsToExclude();

            String subQuery = getObjectsToExcludeSubQuery(objectsToExcludeArray, rowQueryParametars);

            if (!"".equals(subQuery))
            {
                query += " " + SearchConstants.OPERATOR_AND + " " + subQuery;
            }
        }

        return query;
    }

    private String[] getObjectsToExclude()
    {
        Map<String, Object> propertyMap = getPluginSearch().getPluginProperties();

        if (propertyMap.containsKey(SearchConstants.OBJECTS_TO_EXCLUDE))
        {
            String objectsToExclude = (String) propertyMap.get(SearchConstants.OBJECTS_TO_EXCLUDE);

            if (objectsToExclude != null && !"".equals(objectsToExclude))
            {
                return objectsToExclude.split(",");
            }
        }

        return null;
    }

    private String getObjectsToExcludeSubQuery(String[] objectsToExcludeArray, String queryParameters)
    {
        String subQuery = "";

        if (objectsToExcludeArray != null)
        {

            // AFDP-1101: the filter line says, "if the user specifically requested a certain object type, then do not
            // exclude it after all".  We have to check for the URL-encoded search term... Value sent to SOLR is like:
            // {!field f=object_type_s}NOTIFICATION - meaning to include objects of type NOTIFICATION.  The
            // URL-encoded version of this search term is "%21field+f%3Dobject_type_s%7DNOTIFICATION"... so that's
            // what we exclude from the results of this stream.
            subQuery = Arrays.stream(objectsToExcludeArray)
                    .filter((String element) -> !queryParameters.contains("%21field+f%3D" + SearchConstants.PROPERTY_OBJECT_TYPE + "%7D" + element))
                    .map((String element) -> "-" + SearchConstants.PROPERTY_OBJECT_TYPE + ":" + element)
                    .reduce((String left, String right) -> left + " " + SearchConstants.OPERATOR_AND + " " + right)
                    .orElse("");
        }

        return subQuery;
    }


    public String replaceEventTypeName(String solrResult)
    {
        Map<String, Object> propertyMap = getPluginEventType().getPluginProperties();
        for (Map.Entry<String, Object> e : propertyMap.entrySet())
        {
            String key;
            if (e.getKey().contains(SearchConstants.EVENT_TYPE))
            {
                key = e.getKey().replaceFirst(SearchConstants.EVENT_TYPE, "");
            } else
            {
                continue;
            }
            if (solrResult.contains("\"" + key.trim() + "\""))
            {
                solrResult = solrResult.replaceAll("\"" + key + "\"", "\"" + e.getValue() + "\"");
            }
        }
        return solrResult;
    }

    public AcmPlugin getPluginSearch()
    {
        return pluginSearch;
    }

    public void setPluginSearch(AcmPlugin pluginSearch)
    {
        this.pluginSearch = pluginSearch;
    }

    public AcmPlugin getPluginEventType()
    {
        return pluginEventType;
    }

    public void setPluginEventType(AcmPlugin pluginEventType)
    {
        this.pluginEventType = pluginEventType;
    }
}
