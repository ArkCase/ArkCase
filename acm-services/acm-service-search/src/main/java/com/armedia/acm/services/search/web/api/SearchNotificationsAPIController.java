package com.armedia.acm.services.search.web.api;

import com.armedia.acm.pluginmanager.service.AcmPluginManager;
import com.armedia.acm.services.search.model.ApplicationSearchEvent;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.model.solr.SolrResponse;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchEventPublisher;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.StringUtils;
import org.mule.api.MuleException;
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

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class SearchNotificationsAPIController {

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private ExecuteSolrQuery executeSolrQuery;
    private SearchEventPublisher searchEventPublisher;
    private AcmPluginManager acmPluginManager;


    @RequestMapping(
            value = {"/api/v1/plugin/searchNotifications/advanced", "/api/latest/plugin/searchNotifications/advanced"},
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String searchAdvancedObjectByType(
            @RequestParam(value = "s", required = false, defaultValue = "") String sort,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,
            @RequestParam(value = "owner", required = false, defaultValue = "") String owner,
            @RequestParam(value = "newOnly", required = false, defaultValue = "true") boolean newOnly,
            Authentication authentication,
            HttpSession httpSession
    ) throws MuleException {
        String query = "object_type_s:" + "NOTIFICATION";

        if (!StringUtils.isBlank(owner)) {
            query += " AND owner_lcs:" + owner;
        }

        if (newOnly) {
            query += " AND status_lcs:New";
        }

        if (log.isDebugEnabled()) {
            log.debug("Advanced Search: User '" + authentication.getName() + "' is searching for '" + query + "'");
        }

        String results = getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.ADVANCED_SEARCH,
                query, startRow, maxRows, sort);

        publishSearchEvent(authentication, httpSession, true, results);

        return results;
    }

    protected void publishSearchEvent(Authentication authentication,
                                      HttpSession httpSession,
                                      boolean succeeded, String jsonPayload) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        SolrResponse solrResponse = gson.fromJson(jsonPayload, SolrResponse.class);

        if (solrResponse.getResponse() != null) {
            List<SolrDocument> solrDocs = solrResponse.getResponse().getDocs();
            String ipAddress = (String) httpSession.getAttribute("acm_ip_address");
            Long objectId = null;
            for (SolrDocument doc : solrDocs) {
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

    public AcmPluginManager getAcmPluginManager() {
        return acmPluginManager;
    }

    public void setAcmPluginManager(AcmPluginManager acmPluginManager) {
        this.acmPluginManager = acmPluginManager;
    }

    public ExecuteSolrQuery getExecuteSolrQuery() {
        return executeSolrQuery;
    }

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery) {
        this.executeSolrQuery = executeSolrQuery;
    }

    public SearchEventPublisher getSearchEventPublisher() {
        return searchEventPublisher;
    }

    public void setSearchEventPublisher(SearchEventPublisher searchEventPublisher) {
        this.searchEventPublisher = searchEventPublisher;
    }

}
