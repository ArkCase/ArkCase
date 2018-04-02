package com.armedia.acm.services.search.service.solr;

import com.armedia.acm.services.search.model.SolrCore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

public class DefaultSolrPostClient implements SolrPostClient
{

    private String solrHost;
    private int solrPort;
    private String solrContextRoot;
    private String solrUpdateHandler;

    private RestTemplate restTemplate = new RestTemplate();
    private transient final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void sendToSolr(SolrCore core, String json) throws SolrPostException
    {
        Objects.requireNonNull(core, "Core must be specified");
        Objects.requireNonNull(json, "JSON must be specified");
        Objects.requireNonNull(core.getCore(), "The Solr core must have a value");

        String logJson = json != null && json.length() > 50 ? json.substring(0, 50) + "..." : json;
        logger.debug("Sending to Solr core {} with JSON {}", core.getCore(), logJson);

        final String url = String.format("https://%s:%s/%s/%s/%s?overwrite=true",
                getSolrHost(),
                getSolrPort(),
                getSolrContextRoot(),
                core.getCore(),
                getSolrUpdateHandler());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        if (response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError())
        {
            logger.error("Could not post to Solr with JSON {}, got response code {}", logJson, response.getStatusCode().value());
            throw new SolrPostException("Could not post to Solr");
        }

        logger.debug("Sent JSON {} to Solr core {}, got back status code {}", logJson, core, response.getStatusCode().value());

    }

    public String getSolrHost()
    {
        return solrHost;
    }

    public void setSolrHost(String solrHost)
    {
        this.solrHost = solrHost;
    }

    public int getSolrPort()
    {
        return solrPort;
    }

    public void setSolrPort(int solrPort)
    {
        this.solrPort = solrPort;
    }

    public String getSolrContextRoot()
    {
        return solrContextRoot;
    }

    public void setSolrContextRoot(String solrContextRoot)
    {
        this.solrContextRoot = solrContextRoot;
    }

    public String getSolrUpdateHandler()
    {
        return solrUpdateHandler;
    }

    public void setSolrUpdateHandler(String solrUpdateHandler)
    {
        this.solrUpdateHandler = solrUpdateHandler;
    }

}
