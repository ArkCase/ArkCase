package com.armedia.acm.services.search.web.api;

import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.services.search.model.SearchConstants;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by marjan.stefanoski on 17.12.2014.
 */
@Controller
@RequestMapping( { "/api/v1/plugin/search", "/api/latest/plugin/search"} )
public class FacetedSearchAPIController {

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private ExecuteSolrQuery executeSolrQuery;

    private AcmPlugin pluginSearch;

    @RequestMapping(value = "/facetedSearch", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String mainNotFilteredFacetedSerach(
            @RequestParam(value = "q", required = true) String q,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "500") int maxRows,
            @RequestParam(value = "filters", required = false, defaultValue="") String filters,
            Authentication authentication
    ) throws MuleException, UnsupportedEncodingException {
        if ( log.isDebugEnabled() ) {
            log.debug("User '" + authentication.getName() + "' is performing facet search for the query: '" + q + "' ");
        }

        String rowQueryParametars = buildSolrQuery(filters);
        String sort= "";
        String query = SearchConstants.CATCH_ALL_QUERY + q;
        query = URLEncoder.encode(query, "UTF-8");

        String results = getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.ADVANCED_SEARCH,
                query, startRow, maxRows, sort, rowQueryParametars);

        return results;

    }

    private String buildSolrQuery( String filters ) {
        StringBuilder queryBuilder = new StringBuilder();
        Map<String,Object> propertyMap = getPluginSearch().getPluginProperties();
        String jsonString = (String)propertyMap.get(SearchConstants.TIME_PERIOD_KEY);
        JSONArray jsonArray = new JSONArray(jsonString);
        JSONObject timePeriodJSONObject = null;
        for( Map.Entry<String,Object> e: propertyMap.entrySet() ){
            if( e.getKey().contains(SearchConstants.FACET_PRE_KEY) ){
                String facetKey = e.getKey().split(SearchConstants.FACET_PRE_KEY)[1];
                if( e.getKey().contains(SearchConstants.DATE_FACET_PRE_KEY) ){
                    facetKey = e.getKey().split(SearchConstants.DATE_FACET_PRE_KEY)[1];
                    for( int i=0;i<jsonArray.length();i++ ){
                        timePeriodJSONObject = jsonArray.getJSONObject(i);
                        try
                        {
                            String timePeriod = URLEncoder.encode(
                                    "{" + SearchConstants.SOLR_FACET_NAME_CHANGE_COMMAND + "'" + e.getValue() + ", " +
                                            timePeriodJSONObject.getString(SearchConstants.TIME_PERIOD_DESCRIPTION) + "'}", "UTF-8");
                            String timePeriodValue = URLEncoder.encode(timePeriodJSONObject.getString(SearchConstants.TIME_PERIOD_VALUE), "UTF-8");
                            if( queryBuilder.length()>0 ) {
                                queryBuilder.append(SearchConstants.FACET_QUERY_WITH_AND_AS_A_PREFIX + timePeriod +
                                        facetKey + SearchConstants.DOTS_SPLITTER + timePeriodValue);
                            } else {
                                queryBuilder.append(SearchConstants.FACET_QUERY+ timePeriod + facetKey + SearchConstants.DOTS_SPLITTER + timePeriodValue);
                            }
                        } catch (UnsupportedEncodingException e1)
                        {

                            log.error("Encoding problem occur while building SOLR query for faceted search with key: "+facetKey,e1);
                        }

                    }
                } else {
                    String encoded = null;
                    try
                    {
                        encoded = URLEncoder.encode("{" + SearchConstants.SOLR_FACET_NAME_CHANGE_COMMAND + "'" + e.getValue() + "'}", "UTF-8");
                        if(queryBuilder.length()>0) {
                            queryBuilder.append(SearchConstants.FACET_FILED_WITH_AND_AS_A_PREFIX+ encoded + facetKey);
                        } else {
                            queryBuilder.append(SearchConstants.FACET_FILED+ encoded + facetKey);
                        }
                    } catch (UnsupportedEncodingException e1)
                    {
                        log.error("Encoding problem occur while building SOLR query for faceted search with key: " + facetKey, e1);
                    }

                }
            }
        }

        if( !StringUtils.isBlank(filters) ) {
            try {
                filters = URLDecoder.decode(filters, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                log.error("Decoding problem occur while decoding & in the filters part",e);
            }
            String solrFiltersSubQuery = createFacetedFiltersSubString(filters);
            queryBuilder.append(solrFiltersSubQuery);
        }
        return queryBuilder.toString();
    }

    private String createFacetedFiltersSubString(String filters){
        StringBuilder queryBuilder = new StringBuilder();
        Map<String,Object> propertyMap = getPluginSearch().getPluginProperties();
        String jsonString = (String)propertyMap.get(SearchConstants.TIME_PERIOD_KEY);
        JSONArray jsonArray = new JSONArray(jsonString);
        String[] allORFilters = null;
        if ( filters.contains(SearchConstants.AND_SPLITTER) ) {
            String[] fqs = filters.split( SearchConstants.AND_SPLITTER );
            for ( String name : fqs ) {
                String[] filterSplitByQ = name.split(SearchConstants.QUOTE_SPLITTER);
                String[] filterSplitByDots = name.split(SearchConstants.DOTS_SPLITTER);
                if(filterSplitByDots[1].contains("|")){
                    allORFilters  = filterSplitByDots[1].split(SearchConstants.PIPE_SPLITTER);
                }
                for ( Map.Entry<String, Object> mapElement : propertyMap.entrySet() ) {
                    if ( filterSplitByQ[1].equals(mapElement.getValue()) && mapElement.getKey().contains(SearchConstants.FACET_PRE_KEY) ) {
                        if ( mapElement.getKey().contains(SearchConstants.DATE_FACET_PRE_KEY )) {
                            if( allORFilters==null ) {
                                String returnedDateANDSubString = createDateANDQuerySubString(jsonArray, mapElement.getKey(),filterSplitByDots[1].trim());
                                queryBuilder.append(returnedDateANDSubString);
                                break;
                            } else {
                                String returnedDateORSubString = createDateORQuerySubString(allORFilters,jsonArray, mapElement.getKey());
                                queryBuilder.append(returnedDateORSubString);
                                allORFilters = null;
                                break;
                            }
                        } else {
                            if(allORFilters!=null) {
                                String returnedRegularORSubString = createRegularORQuerySubString(allORFilters, mapElement.getKey());
                                queryBuilder.append(returnedRegularORSubString);
                                allORFilters = null;
                                break;
                            } else {
                                String returnedRegularANDSubString = createRegularANDQuerySubString(mapElement.getKey(),filterSplitByDots[1].trim());
                                queryBuilder.append(returnedRegularANDSubString);
                                allORFilters = null;
                                break;
                            }
                        }
                    }
                }
            }
        } else {
            String[] filterSplitByQ = filters.split(SearchConstants.QUOTE_SPLITTER);
            String[] filterSplitByDots = filters.split(SearchConstants.DOTS_SPLITTER);
            if( filterSplitByDots[1].contains("|") ){
                allORFilters  = filterSplitByDots[1].split(SearchConstants.PIPE_SPLITTER);
            }
            for ( Map.Entry<String, Object> e : propertyMap.entrySet() ) {
                if ( filterSplitByQ[1].equals(e.getValue()) && e.getKey().contains(SearchConstants.FACET_PRE_KEY) ) {
                    if ( e.getKey().contains(SearchConstants.DATE_FACET_PRE_KEY) ) {
                        if( allORFilters==null ) {
                            String returnedDateANDSubString = createDateANDQuerySubString(jsonArray,e.getKey(),filterSplitByDots[1].trim());
                            queryBuilder.append(returnedDateANDSubString);
                            break;
                        } else {
                            String returnedDateORSubString = createDateORQuerySubString(allORFilters,jsonArray,e.getKey());
                            queryBuilder.append(returnedDateORSubString);
                            break;
                        }
                    } else {
                        if( allORFilters==null ) {
                            String returnedRegularANDQuerySubString = createRegularANDQuerySubString(e.getKey(), filterSplitByDots[1].trim());
                            queryBuilder.append(returnedRegularANDQuerySubString);
                            break;
                        } else {
                            String returnedRegularORSubstring = createRegularORQuerySubString(allORFilters,e.getKey());
                            queryBuilder.append(returnedRegularORSubstring);
                            break;
                        }
                    }
                }
            }
        }
        return queryBuilder.toString();
    }

    private String createDateORQuerySubString(String[] allORFilters,JSONArray jsonArrayOfDatePeriods, String filterKey){
            String substitutionName = filterKey.split(SearchConstants.DATE_FACET_PRE_KEY)[1];
            String value = null;
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append(SearchConstants.SOLR_FILTER_QUERY_ATTRIBUTE_NAME);
            boolean isFirst = true;
        try {
        for( String orFilter : allORFilters ){
                for( int i =0;i<jsonArrayOfDatePeriods.length(); i++ ) {
                    if( jsonArrayOfDatePeriods.getJSONObject(i).getString(SearchConstants.TIME_PERIOD_DESCRIPTION).equals(orFilter.trim())) {
                        value = jsonArrayOfDatePeriods.getJSONObject(i).getString(SearchConstants.TIME_PERIOD_VALUE);
                        break;
                    }
                }
                if( isFirst ) {
                    isFirst = false;
                    queryBuilder.append(URLEncoder.encode(substitutionName + SearchConstants.DOTS_SPLITTER, "UTF-8") + URLEncoder.encode("("+value, "UTF-8"));
                } else {
                    queryBuilder.append(URLEncoder.encode(" OR ", "UTF-8") + URLEncoder.encode(value, "UTF-8"));
                }
            }
            queryBuilder.append(URLEncoder.encode(")","UTF-8"));
        } catch ( UnsupportedEncodingException e1 ) {
            log.error("Encoding problem occur while building date OR SOLR query sub-string", e1);
        }
        return queryBuilder.toString();
    }

    private String createRegularORQuerySubString(String[] allORFilters, String filterKey){
        String substitutionName = filterKey.split(SearchConstants.FACET_PRE_KEY)[1];
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(SearchConstants.SOLR_FILTER_QUERY_ATTRIBUTE_NAME);
        boolean isFirst = true;
        for( String orFilter : allORFilters ){
            try {
                if( isFirst ) {
                    isFirst = false;
                    queryBuilder.append(URLEncoder.encode("_query_:\"{!term f=" + substitutionName + "}", "UTF-8") + URLEncoder.encode(orFilter.trim()+"\"", "UTF-8"));
                } else {
                    queryBuilder.append(URLEncoder.encode(" OR _query_:\"{!term f=" + substitutionName + "}", "UTF-8") + URLEncoder.encode(orFilter.trim()+"\"", "UTF-8"));
                }
            } catch (UnsupportedEncodingException e1) {
                if(log.isErrorEnabled()) {
                    log.error("Encoding problem occur while building regular OR SOLR query sub-string", e1);
                }
            }
        }
        return  queryBuilder.toString();
    }

    private String createRegularANDQuerySubString(String filterKey, String filterValue){
        StringBuilder queryBuilder = new StringBuilder();
        String substitutionName = filterKey.split(SearchConstants.FACET_PRE_KEY)[1];
        try {
            queryBuilder.append(SearchConstants.SOLR_FILTER_QUERY_ATTRIBUTE_NAME + URLEncoder.encode("{!term f=" + substitutionName + "}", "UTF-8") + URLEncoder.encode(filterValue, "UTF-8"));
        } catch ( UnsupportedEncodingException e ) {
            if( log.isErrorEnabled() ) {
                log.error("Encoding problem occur while building regular AND SOLR query sub-string", e);
            }
        }
        return queryBuilder.toString();
    }

    private String createDateANDQuerySubString(JSONArray jsonArray,String filterKey, String filterValue ) {
        StringBuilder queryBuilder = new StringBuilder();
        String substitutionName = filterKey.split(SearchConstants.DATE_FACET_PRE_KEY)[1];
        String value = null;
        for( int i =0;i<jsonArray.length(); i++ ) {
            if( jsonArray.getJSONObject(i).getString(SearchConstants.TIME_PERIOD_DESCRIPTION).equals(filterValue.trim()))
                value = jsonArray.getJSONObject(i).getString(SearchConstants.TIME_PERIOD_VALUE);
        }
        try {
            queryBuilder.append(SearchConstants.SOLR_FILTER_QUERY_ATTRIBUTE_NAME + URLEncoder.encode(substitutionName + SearchConstants.DOTS_SPLITTER, "UTF-8") + URLEncoder.encode(value, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            if( log.isErrorEnabled() ) {
                log.error("Encoding problem occur while building date AND SOLR query sub-string", e);
            }
        }
        return queryBuilder.toString();
    }

    public AcmPlugin getPluginSearch() {
        return pluginSearch;
    }

    public void setPluginSearch(AcmPlugin pluginSearch) {
        this.pluginSearch = pluginSearch;
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
