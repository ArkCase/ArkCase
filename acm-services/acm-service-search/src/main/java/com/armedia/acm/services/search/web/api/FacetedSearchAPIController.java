package com.armedia.acm.services.search.web.api;

import com.armedia.acm.pluginmanager.model.AcmPlugin;
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
import javax.servlet.http.HttpSession;
import java.beans.Encoder;
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

    @RequestMapping(value = "/facetedSearch", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String mainNotFilteredFacetedSerach(
            @RequestParam(value = "q", required = true) String q,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "500") int maxRows,
            @RequestParam(value = "filters", required = false, defaultValue="none") String filters,
            Authentication authentication,
            HttpServletResponse httpResponse
    ) throws MuleException {

        if ( log.isDebugEnabled() ) {
            log.debug("User '" + authentication.getName() + "' is performing facet search for the query: '" + q + "' ");
        }

        String rowQueryParametars = buildSolrQuery(filters).replace(" ", "+");
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

    private String buildSolrQuery(String filters){
        StringBuilder queryBuilder = new StringBuilder();
        //queryBuilder.append("facet=true");
        Map<String,Object> propertyMap = getPluginSearch().getPluginProperties();
        for(Map.Entry<String,Object> e: propertyMap.entrySet()){
            if(e.getKey().contains("facet.")){
                String facetKey = e.getKey().split("facet.")[1];
                if(facetKey.contains("date") || facetKey.contains("Date")){
                    if(queryBuilder.length()>0) {
                        try {
                            queryBuilder.append("&facet.query="+URLEncoder.encode("{!key='" + (String) e.getValue() + ", Previous Week'}","UTF-8") + facetKey + ":"+URLEncoder.encode("[NOW/DAY-7DAY TO *]","UTF-8"));
                        } catch (UnsupportedEncodingException e1) {
                            log.error("Encoding problem occur while building SOLR query for faceted search with key: "+facetKey,e1);
                        }
                    } else {
                        try {
                            queryBuilder.append("facet.query="+URLEncoder.encode("{!key='" + (String) e.getValue() + ", Previous Week'}","UTF-8") + facetKey + ":"+URLEncoder.encode("[NOW/DAY-7DAY TO *]","UTF-8"));
                        } catch (UnsupportedEncodingException e1) {
                            log.error("Encoding problem occur while building SOLR query for faceted search with key: "+facetKey,e1);
                        }
                    }
                    try {
                        queryBuilder.append("&facet.query="+URLEncoder.encode("{!key='" + (String) e.getValue() + ", Previous Month'}","UTF-8")+facetKey+":"+URLEncoder.encode("[NOW/DAY-1MONTH TO *]","UTF-8"));
                        queryBuilder.append("&facet.query="+URLEncoder.encode("{!key='" + (String) e.getValue() + ", Previous Year'}","UTF-8")+facetKey+":"+URLEncoder.encode("[NOW/DAY-1YEAR TO *]","UTF-8"));
                    } catch (UnsupportedEncodingException e1) {
                        log.error("Encoding problem occur while building SOLR query for faceted search with key: "+facetKey,e1);
                    }
                } else {
                    if(queryBuilder.length()>0) {
                        try {
                            queryBuilder.append("&facet.field="+URLEncoder.encode("{!key='" + (String) e.getValue() + "'}","UTF-8") + facetKey);
                        } catch (UnsupportedEncodingException e1) {
                            log.error("Encoding problem occur while building SOLR query for faceted search with key: " + facetKey, e1);
                        }
                    } else {
                        try {
                            queryBuilder.append("facet.field="+URLEncoder.encode("{!key='" + (String) e.getValue() + "'}","UTF-8") + facetKey);
                        } catch (UnsupportedEncodingException e1) {
                            log.error("Encoding problem occur while building SOLR query for faceted search with key: " + facetKey, e1);
                        }
                    }
                }
            }
        }
        if(!"none".equals(filters)){
            String substitutionName;
            if(filters.contains("&")) {
                String[] fqs = filters.split("&");
                for(String name: fqs) {
                    for(Map.Entry<String,Object> e: propertyMap.entrySet()){
                        String[] filterSplitByQ = name.split("\"");
                        String[] filterSplitByDots = name.split(":");
                        if(filterSplitByQ[1].equals(e.getValue()) && e.getKey().contains("facet.")){
                            substitutionName = e.getKey().split("facet.")[1];
                            try {
                                String logString = "&fq="+URLEncoder.encode("{!term f="+substitutionName+"}","UTF-8")+URLEncoder.encode(filterSplitByDots[1],"UTF-8");
                                System.out.println("1 ONE   :"+logString);
                                queryBuilder.append("&fq="+URLEncoder.encode("{!term f="+substitutionName+"}","UTF-8")+URLEncoder.encode(filterSplitByDots[1],"UTF-8"));
                            } catch (UnsupportedEncodingException e1) {
                                log.error("Encoding problem occur while building SOLR query", e1);
                            }
                        }
                    }
                }
            } else {
                String[] filterSplitByQ = filters.split("\"");
                String[] filterSplitByDots = filters.split(":");
                for(Map.Entry<String,Object> e: propertyMap.entrySet()){
                    if(filterSplitByQ[1].equals(e.getValue()) && e.getKey().contains("facet.")){
                        substitutionName = e.getKey().split("facet.")[1];
                        try {
                            String logString = "&fq="+URLEncoder.encode("{!term f="+substitutionName+"}","UTF-8")+URLEncoder.encode(filterSplitByDots[1],"UTF-8");
                            System.out.println("2 TWO   : "+logString);
                            queryBuilder.append("&fq="+URLEncoder.encode("{!term f="+substitutionName+"}","UTF-8")+URLEncoder.encode(filterSplitByDots[1],"UTF-8"));
                        } catch (UnsupportedEncodingException e1) {
                            log.error("Encoding problem occur while building SOLR query", e1);
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
