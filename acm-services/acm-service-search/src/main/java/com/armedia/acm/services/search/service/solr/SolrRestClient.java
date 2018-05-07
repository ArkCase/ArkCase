package com.armedia.acm.services.search.service.solr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class SolrRestClient
{

    private String solrHost;
    private int solrPort;
    private String solrContextRoot;

    private transient final RestTemplate restTemplate = new RestTemplate();

    private transient final Logger logger = LoggerFactory.getLogger(getClass());

    public void postToSolr(String core, String contentHandler, HttpEntity<? extends Object> entity, String logText, String extraUrlParams)
            throws SolrPostException
    {
        String url = String.format("https://%s:%s/%s/%s/%s?overwrite=true",
                getSolrHost(),
                getSolrPort(),
                getSolrContextRoot(),
                core,
                contentHandler);
        if (extraUrlParams != null && !extraUrlParams.trim().isEmpty())
        {
            url += extraUrlParams.startsWith("&") ? extraUrlParams : "&" + extraUrlParams;
        }

        try
        {

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            if (isRecoverable(response.getStatusCode()))
            {
                logger.error("Could not post to Solr: [{}], got response code {}", logText, response.getStatusCode().value());
                throw new SolrPostException(String.format("Could not post [%s] to Solr: %s", logText, response.getStatusCode()));
            }

            logger.debug("Posted to Solr: [{}], got response code {}", logText, response.getStatusCode().value());
        }
        catch (HttpStatusCodeException e)
        {
            if (isRecoverable(e.getStatusCode()))
            {
                throw new SolrPostException(String.format("Could not post [%s] to Solr: %s", logText, e.getMessage()));
            }
            else
            {
                logger.error("Could not send [{}] to Solr, got an unrecoverable error {}", logText, e.getStatusCode());
            }
        }
        catch (RestClientException e)
        {
            throw new SolrPostException(String.format("Could not post [%s] to Solr: %s", logText, e.getMessage()));
        }
    }

    /**
     * Is the exception such that the request may succeed if we try again?
     * 
     * @param e
     * @return
     */
    private boolean isRecoverable(HttpStatus status)
    {
        return status.is5xxServerError()
                || HttpStatus.NOT_FOUND.equals(status)
                || HttpStatus.CONFLICT.equals(status)
                || HttpStatus.TOO_MANY_REQUESTS.equals(status);
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

}
