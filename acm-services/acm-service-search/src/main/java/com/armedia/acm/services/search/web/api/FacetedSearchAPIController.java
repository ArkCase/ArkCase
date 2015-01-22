package com.armedia.acm.services.search.web.api;

import com.armedia.acm.pluginmanager.model.AcmPlugin;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by marjan.stefanoski on 17.12.2014.
 */
@Controller
@RequestMapping( { "/api/v1/plugin/search", "/api/latest/plugin/search"} )
public class FacetedSearchAPIController {

    private Logger log = LoggerFactory.getLogger(getClass());

    private MuleClient muleClient;
    private AcmPlugin pluginSearch;

    private static final String DATE_FACET_PRE_KEY = "facet.date.";
    private static final String FACET_PRE_KEY = "facet.";

    private static final String FACET_FILED = "facet.field=";
    private static final String FACET_FILED_WITH_AND_AS_A_PREFIX = "&facet.field=";
    private static final String FACET_QUERY = "facet.query=";
    private static final String FACET_QUERY_WITH_AND_AS_A_PREFIX = "&facet.query=";

    private static final String SOLR_FILTER_QUERY_ATTRIBUTE_NAME = "&fq=";
    private static final String SOLR_FACET_NAME_CHANGE_COMMAND = "!key=";


    private static final String TIME_PERIOD_KEY = "search.time.period";
    private static final String TIME_PERIOD_DESCRIPTION = "desc";
    private static final String TIME_PERIOD_VALUE = "value";

    private static final String QUOTE_SPLITTER = "\"";
    private static final String DOTS_SPLITTER = ":";
    private static final String PIPE_SPLITTER = "\\|";
    private static final String AND_SPLITTER = "&";

    @RequestMapping(value = "/facetedSearch", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String mainNotFilteredFacetedSerach(
            @RequestParam(value = "q", required = true) String q,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "500") int maxRows,
            @RequestParam(value = "filters", required = false, defaultValue="") String filters,
            Authentication authentication,
            HttpServletResponse httpResponse
    ) throws MuleException {
        if ( log.isDebugEnabled() ) {
            log.debug("User '" + authentication.getName() + "' is performing facet search for the query: '" + q + "' ");
        }

        String rowQueryParametars = buildSolrQuery(filters);
        String sort= "";

        Map<String, Object> headers = new HashMap<>();
        headers.put("query", URLEncoder.encode(q));
        headers.put("firstRow", startRow);
        headers.put("maxRows", maxRows);
        headers.put("sort", sort);
        headers.put("rowQueryParametars",rowQueryParametars);

        MuleMessage response = getMuleClient().send("vm://advancedSearchQuery.in", "", headers);

        log.debug("Response type: " + response.getPayload().getClass());

        if ( response.getPayload() instanceof String ) {
           // httpResponse.addHeader("X-JSON", response.getPayload().toString());
            return (String) response.getPayload();
        }

        throw new IllegalStateException("Unexpected payload type: " + response.getPayload().getClass().getName());

    }

    private String buildSolrQuery( String filters ) {
        StringBuilder queryBuilder = new StringBuilder();
        Map<String,Object> propertyMap = getPluginSearch().getPluginProperties();
        String jsonString = (String)propertyMap.get(TIME_PERIOD_KEY);
        JSONArray jsonArray = new JSONArray(jsonString);
        JSONObject timePeriodJSONObject = null;
        for( Map.Entry<String,Object> e: propertyMap.entrySet() ){
            if( e.getKey().contains(FACET_PRE_KEY) ){
                String facetKey = e.getKey().split(FACET_PRE_KEY)[1];
                if(e.getKey().contains(DATE_FACET_PRE_KEY)){
                    facetKey = e.getKey().split(DATE_FACET_PRE_KEY)[1];
                    for( int i=0;i<jsonArray.length();i++ ){
                        timePeriodJSONObject = jsonArray.getJSONObject(i);
                        if( queryBuilder.length()>0 ) {
                            try {
                                queryBuilder.append(FACET_QUERY_WITH_AND_AS_A_PREFIX + URLEncoder.encode("{"+SOLR_FACET_NAME_CHANGE_COMMAND+"'" + (String) e.getValue() +", "+ timePeriodJSONObject.getString(TIME_PERIOD_DESCRIPTION)+"'}","UTF-8") + facetKey + DOTS_SPLITTER + URLEncoder.encode(timePeriodJSONObject.getString(TIME_PERIOD_VALUE),"UTF-8"));
                            } catch (UnsupportedEncodingException e1) {
                                log.error("Encoding problem occur while building SOLR query for faceted search with key: "+facetKey,e1);
                            }
                        } else {
                            try {
                                queryBuilder.append(FACET_QUERY+URLEncoder.encode("{"+SOLR_FACET_NAME_CHANGE_COMMAND+"'" + (String) e.getValue() +", "+ timePeriodJSONObject.getString(TIME_PERIOD_DESCRIPTION)+"'}","UTF-8") + facetKey + DOTS_SPLITTER+URLEncoder.encode(timePeriodJSONObject.getString(TIME_PERIOD_VALUE),"UTF-8"));
                            } catch (UnsupportedEncodingException e1) {
                                log.error("Encoding problem occur while building SOLR query for faceted search with key: "+facetKey,e1);
                            }
                        }
                    }
                } else {
                    if(queryBuilder.length()>0) {
                        try {
                            queryBuilder.append(FACET_FILED_WITH_AND_AS_A_PREFIX+URLEncoder.encode("{"+SOLR_FACET_NAME_CHANGE_COMMAND+"'" + (String) e.getValue() + "'}","UTF-8") + facetKey);
                        } catch (UnsupportedEncodingException e1) {
                            log.error("Encoding problem occur while building SOLR query for faceted search with key: " + facetKey, e1);
                        }
                    } else {
                        try {
                            queryBuilder.append(FACET_FILED+URLEncoder.encode("{"+SOLR_FACET_NAME_CHANGE_COMMAND+"'" + (String) e.getValue() + "'}","UTF-8") + facetKey);
                        } catch (UnsupportedEncodingException e1) {
                            log.error("Encoding problem occur while building SOLR query for faceted search with key: " + facetKey, e1);
                        }
                    }
                }
            }
        }

        if( !StringUtils.isBlank(filters) ) {
            String solrFiltersSubQuery = createFacetedFiltersSubString(filters);
            queryBuilder.append(solrFiltersSubQuery);
        }
        return queryBuilder.toString();
    }

    private String createFacetedFiltersSubString(String filters){
        StringBuilder queryBuilder = new StringBuilder();
        Map<String,Object> propertyMap = getPluginSearch().getPluginProperties();
        String jsonString = (String)propertyMap.get(TIME_PERIOD_KEY);
        JSONArray jsonArray = new JSONArray(jsonString);
        String[] allORFilters = null;
        if ( filters.contains(AND_SPLITTER) ) {
            String[] fqs = filters.split( AND_SPLITTER );
            for ( String name : fqs ) {
                String[] filterSplitByQ = name.split(QUOTE_SPLITTER);
                String[] filterSplitByDots = name.split(DOTS_SPLITTER);
                if(filterSplitByDots[1].contains("|")){
                    allORFilters  = filterSplitByDots[1].split(PIPE_SPLITTER);
                }
                for ( Map.Entry<String, Object> mapElement : propertyMap.entrySet() ) {
                    if ( filterSplitByQ[1].equals(mapElement.getValue()) && mapElement.getKey().contains(FACET_PRE_KEY) ) {
                        if ( mapElement.getKey().contains(DATE_FACET_PRE_KEY )) {
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
            String[] filterSplitByQ = filters.split(QUOTE_SPLITTER);
            String[] filterSplitByDots = filters.split(DOTS_SPLITTER);
            if( filterSplitByDots[1].contains("|") ){
                allORFilters  = filterSplitByDots[1].split(PIPE_SPLITTER);
            }
            for ( Map.Entry<String, Object> e : propertyMap.entrySet() ) {
                if ( filterSplitByQ[1].equals(e.getValue()) && e.getKey().contains(FACET_PRE_KEY) ) {
                    if ( e.getKey().contains(DATE_FACET_PRE_KEY) ) {
                        if( allORFilters==null ) {
                            String returnedDateANDSubString = createDateANDQuerySubString(jsonArray,e.getKey(),filterSplitByDots[1].trim());
                            queryBuilder.append(returnedDateANDSubString);
                        } else {
                            String returnedDateORSubString = createDateORQuerySubString(allORFilters,jsonArray,e.getKey());
                            queryBuilder.append(returnedDateORSubString);
                        }
                    } else {
                        String returnedRegularANDQuerySubString = createRegularANDQuerySubString(e.getKey(),filterSplitByDots[1].trim());
                        queryBuilder.append(returnedRegularANDQuerySubString);
                    }
                }
            }
        }
        return queryBuilder.toString();
    }

    private String createDateORQuerySubString(String[] allORFilters,JSONArray jsonArrayOfDatePeriods, String filterKey){
            String substitutionName = filterKey.split(DATE_FACET_PRE_KEY)[1];
            String value = null;
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append(SOLR_FILTER_QUERY_ATTRIBUTE_NAME);
            boolean isFirst = true;
        try {
        for( String orFilter : allORFilters ){
                for( int i =0;i<jsonArrayOfDatePeriods.length(); i++ ) {
                    if( jsonArrayOfDatePeriods.getJSONObject(i).getString(TIME_PERIOD_DESCRIPTION).equals(orFilter.trim())) {
                        value = jsonArrayOfDatePeriods.getJSONObject(i).getString(TIME_PERIOD_VALUE);
                        break;
                    }
                }
                if( isFirst ) {
                    isFirst = false;
                    queryBuilder.append(URLEncoder.encode(substitutionName + DOTS_SPLITTER, "UTF-8") + URLEncoder.encode("("+value, "UTF-8"));
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
        String substitutionName = filterKey.split(FACET_PRE_KEY)[1];
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(SOLR_FILTER_QUERY_ATTRIBUTE_NAME);
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
        String substitutionName = filterKey.split(FACET_PRE_KEY)[1];
        try {
            queryBuilder.append(SOLR_FILTER_QUERY_ATTRIBUTE_NAME + URLEncoder.encode("{!term f=" + substitutionName + "}", "UTF-8") + URLEncoder.encode(filterValue, "UTF-8"));
        } catch ( UnsupportedEncodingException e ) {
            if( log.isErrorEnabled() ) {
                log.error("Encoding problem occur while building regular AND SOLR query sub-string", e);
            }
        }
        return queryBuilder.toString();
    }

    private String createDateANDQuerySubString(JSONArray jsonArray,String filterKey, String filterValue ) {
        StringBuilder queryBuilder = new StringBuilder();
        String substitutionName = filterKey.split(DATE_FACET_PRE_KEY)[1];
        String value = null;
        for( int i =0;i<jsonArray.length(); i++ ) {
            if( jsonArray.getJSONObject(i).getString(TIME_PERIOD_DESCRIPTION).equals(filterValue.trim()))
                value = jsonArray.getJSONObject(i).getString(TIME_PERIOD_VALUE);
        }
        try {
            queryBuilder.append(SOLR_FILTER_QUERY_ATTRIBUTE_NAME + URLEncoder.encode(substitutionName + DOTS_SPLITTER, "UTF-8") + URLEncoder.encode(value, "UTF-8"));
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

    public MuleClient getMuleClient() {
        return muleClient;
    }

    public void setMuleClient(MuleClient muleClient) {
        this.muleClient = muleClient;
    }
}
