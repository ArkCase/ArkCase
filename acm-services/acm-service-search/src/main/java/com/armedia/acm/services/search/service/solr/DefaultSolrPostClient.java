package com.armedia.acm.services.search.service.solr;

import com.armedia.acm.services.search.model.SolrCore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Objects;

public class DefaultSolrPostClient implements SolrPostClient
{

    private String solrUpdateHandler;
    private SolrRestClient solrRestClient;

    private transient final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void sendToSolr(SolrCore core, String json) throws SolrPostException
    {
        Objects.requireNonNull(core, "Core must be specified");
        Objects.requireNonNull(json, "JSON must be specified");
        Objects.requireNonNull(core.getCore(), "The Solr core must have a value");

        String logJson = json != null && json.length() > 50 ? json.substring(0, 50) + "..." : json;
        logger.debug("Sending to Solr core {} with JSON {}", core.getCore(), logJson);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        getSolrRestClient().postToSolr(core.getCore(), getSolrUpdateHandler(), entity, logJson, null);
    }

    public String getSolrUpdateHandler()
    {
        return solrUpdateHandler;
    }

    public void setSolrUpdateHandler(String solrUpdateHandler)
    {
        this.solrUpdateHandler = solrUpdateHandler;
    }

    public SolrRestClient getSolrRestClient()
    {
        return solrRestClient;
    }

    public void setSolrRestClient(SolrRestClient solrRestClient)
    {
        this.solrRestClient = solrRestClient;
    }

}
