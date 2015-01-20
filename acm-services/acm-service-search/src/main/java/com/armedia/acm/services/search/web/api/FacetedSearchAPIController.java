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
            String substitutionName;
            if ( filters.contains("&") ) {
                String[] fqs = filters.split("&");
                for ( String name : fqs ) {
                    for ( Map.Entry<String, Object> e : propertyMap.entrySet() ) {
                        String[] filterSplitByQ = name.split(QUOTE_SPLITTER);
                        String[] filterSplitByDots = name.split(DOTS_SPLITTER);
                        if (filterSplitByQ[1].equals(e.getValue()) && e.getKey().contains(FACET_PRE_KEY)) {
                            if (e.getKey().contains(DATE_FACET_PRE_KEY)) {
                                substitutionName = e.getKey().split(DATE_FACET_PRE_KEY)[1];
                                String value = null;
                                for( int i =0;i<jsonArray.length(); i++ ) {
                                 if( jsonArray.getJSONObject(i).getString(TIME_PERIOD_DESCRIPTION).equals(name.split(DOTS_SPLITTER)[1]))
                                    value = jsonArray.getJSONObject(i).getString(TIME_PERIOD_VALUE);
                                }
                                try {
                                    queryBuilder.append(SOLR_FILTER_QUERY_ATTRIBUTE_NAME + URLEncoder.encode(substitutionName + DOTS_SPLITTER, "UTF-8") + URLEncoder.encode(value, "UTF-8"));
                                } catch (UnsupportedEncodingException e1) {
                                    log.error("Encoding problem occur while building SOLR query", e1);
                                }
                            } else {
                                substitutionName = e.getKey().split(FACET_PRE_KEY)[1];
                                try {
                                    queryBuilder.append(SOLR_FILTER_QUERY_ATTRIBUTE_NAME + URLEncoder.encode("{!term f=" + substitutionName + "}", "UTF-8") + URLEncoder.encode(filterSplitByDots[1], "UTF-8"));
                                } catch (UnsupportedEncodingException e1) {
                                    log.error("Encoding problem occur while building SOLR query", e1);
                                }
                            }
                        }
                    }
                }
            } else {
                String[] filterSplitByQ = filters.split(QUOTE_SPLITTER);
                String[] filterSplitByDots = filters.split(DOTS_SPLITTER);
                for (Map.Entry<String, Object> e : propertyMap.entrySet()) {
                    if (filterSplitByQ[1].equals(e.getValue()) && e.getKey().contains(FACET_PRE_KEY)) {
                        if (e.getKey().contains(DATE_FACET_PRE_KEY)) {
                            substitutionName = e.getKey().split(DATE_FACET_PRE_KEY)[1];
                            try {
                                queryBuilder.append(SOLR_FILTER_QUERY_ATTRIBUTE_NAME + URLEncoder.encode(substitutionName + DOTS_SPLITTER, "UTF-8") + URLEncoder.encode(filterSplitByDots[1], "UTF-8"));
                            } catch (UnsupportedEncodingException e1) {
                                log.error("Encoding problem occur while building SOLR query", e1);
                            }
                        } else {
                            substitutionName = e.getKey().split("facet.")[1];
                            try {
                                queryBuilder.append(SOLR_FILTER_QUERY_ATTRIBUTE_NAME + URLEncoder.encode("{!term f=" + substitutionName + "}", "UTF-8") + URLEncoder.encode(filterSplitByDots[1], "UTF-8"));
                            } catch (UnsupportedEncodingException e1) {
                                log.error("Encoding problem occur while building SOLR query", e1);
                            }
                        }
                    }
                }
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
