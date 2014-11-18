package com.armedia.acm.services.search.web.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.armedia.acm.services.search.model.ApplicationSearchEvent;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.model.solr.SolrResponse;
import com.armedia.acm.services.search.service.SearchEventPublisher;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Controller
@RequestMapping( { "/api/v1/plugin/search", "/api/latest/plugin/search"} )
public class SearchObjectByTypeAPIController {

    private Logger log = LoggerFactory.getLogger(getClass());

    private MuleClient muleClient;
    private SearchEventPublisher searchEventPublisher;

    @RequestMapping(value = "/{objectType}", method  = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String searchObjectByType(
    		@PathVariable("objectType") String objectType,
            @RequestParam(value = "s", required = false, defaultValue = "") String sort,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,
            @RequestParam(value = "assignee", required = false, defaultValue = "") String assignee,
            @RequestParam(value = "activeOnly", required = false, defaultValue = "true") boolean activeOnly,
            Authentication authentication,
            HttpSession httpSession
    ) throws MuleException, Exception
    {
        String query = "object_type_s:" + objectType;  
        
        if (!StringUtils.isBlank(assignee)) {
            query += " AND assignee_s:" + assignee;
        }

        if ( activeOnly )
        {
            query += " AND -status_s:COMPLETE AND -status_s:DELETE AND -status_s:CLOSED";
        }
        
        if ( log.isDebugEnabled() )
        {
            log.debug("User '" + authentication.getName() + "' is searching for '" + query + "'");
        }
     
        Map<String, Object> headers = new HashMap<>();
        headers.put("query", query);
        headers.put("firstRow", startRow);
        headers.put("maxRows", maxRows);
        headers.put("sort", sort);

        MuleMessage response = getMuleClient().send("vm://quickSearchQuery.in", "", headers);

        log.debug("Response type: " + response.getPayload().getClass());

        if ( response.getPayload() instanceof String )
        {
            String responsePayload = (String) response.getPayload();
           
            publishSearchEvent(authentication, httpSession, true, responsePayload);
          
            return responsePayload;
        }

        throw new IllegalStateException("Unexpected payload type: " + response.getPayload().getClass().getName());
    }
    
    protected void publishSearchEvent(Authentication authentication,
            HttpSession httpSession,
            boolean succeeded, String jsonPayload)
    {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        SolrResponse solrResponse = gson.fromJson(jsonPayload, SolrResponse.class);
        
        if ( solrResponse.getResponse() != null ) {
            List<SolrDocument> solrDocs = solrResponse.getResponse().getDocs();
            String ipAddress = (String) httpSession.getAttribute("acm_ip_address");
            Long objectId = null;
            for ( SolrDocument doc : solrDocs ) {
                // in case when objectID is not Long like in USER case
                try {
                    objectId = Long.parseLong(doc.getObject_id_s());
                } catch (NumberFormatException e) {
                    objectId = new Long(-1);
                }
                ApplicationSearchEvent event = new ApplicationSearchEvent(objectId, doc.getObject_type_s(),
                        authentication.getName(), succeeded, ipAddress);
                getSearchEventPublisher().publishSearchEvent(event);
            }
        }
    }
    
    public MuleClient getMuleClient()
    {
        return muleClient;
    }

    public void setMuleClient(MuleClient muleClient)
    {
        this.muleClient = muleClient;
    }

    public SearchEventPublisher getSearchEventPublisher() {
        return searchEventPublisher;
    }

    public void setSearchEventPublisher(SearchEventPublisher searchEventPublisher) {
        this.searchEventPublisher = searchEventPublisher;
    }
   
}
