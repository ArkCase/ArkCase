package com.armedia.acm.services.search.service.solr;

/*-
 * #%L
 * ACM Service: Search
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.services.search.model.solr.SolrConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class SolrRestClient
{

    private transient final RestTemplate restTemplate = new RestTemplate();
    private transient final Logger logger = LogManager.getLogger(getClass());
    private SolrConfig solrConfig;
    private JmsTemplate jmsTemplate;
    private final String DLQPrefix = "DLQ.";

    public void postToSolr(String destinationQueue, String core, String contentHandler, HttpEntity<InputStreamResource> entity,
            String logText,
            String urlWithPlaceholders, Map<String, Object> urlValues)
            throws SolrPostException
    {
        String url = String.format("https://%s:%s/%s/%s/%s?overwrite=true&%s",
                solrConfig.getHost(), solrConfig.getPort(), solrConfig.getContextRoot(), core, contentHandler, urlWithPlaceholders);

        String destinationName = DLQPrefix + destinationQueue;

        try
        {
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class, urlValues);
            // If we get 4xx or 5xx statusCode without throwing exception we send the message to DLQ
            // so the admin can view the message and investigate the result
            if (isUnRecoverable(response.getStatusCode()))
            {
                logger.error("Could not post to Solr: [{}], got response code {}, sending to DLQ.[%s] queue",
                        logText, response.getStatusCode().value(), core);
                getJmsTemplate().convertAndSend(destinationName, entity.getBody());
            }

            logger.debug("Posted to Solr: [{}], got response code {}", logText, response.getStatusCode().value());
        }

        // If we get HttpStatusCodeException we send the message to DLQ so the admin
        // can view the message and investigate it
        catch (HttpStatusCodeException e)
        {
            logger.error("Could not post to Solr: [{}],  sending to DLQ.[%s] queue",
                    logText, core);
            getJmsTemplate().convertAndSend(destinationName, entity.getBody());
        }
        // If we catch RestClientException we will propagate the error to crash the whole transaction
        // and message to stay in the main queue(ex:solrAdvancedSearch.in)
        catch (RestClientException e)
        {
            throw new SolrPostException(String.format("Could not post [%s] to Solr: %s", logText, e.getMessage()));
        }

    }

    public void postToSolr(String destinationQueue, String core, String contentHandler, HttpEntity<? extends Object> entity, String logText)
            throws SolrPostException
    {
        String url = String.format("https://%s:%s/%s/%s/%s?overwrite=true",
                solrConfig.getHost(), solrConfig.getPort(), solrConfig.getContextRoot(), core, contentHandler);

        String destinationName = DLQPrefix + destinationQueue;

        try
        {

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            // If we get 4xx or 5xx statusCode without throwing exception we send the message to DLQ
            // so the admin can view the message and investigate it
            if (isUnRecoverable(response.getStatusCode()))
            {
                logger.error("Could not post to Solr: [{}], got response code {}, sending to DLQ.[%s] queue",
                        logText, response.getStatusCode().value(), core);
                getJmsTemplate().convertAndSend(destinationName, entity.getBody());
            }

            logger.debug("Posted to Solr: [{}], got response code {}", logText, response.getStatusCode().value());
        }
        catch (HttpStatusCodeException e)
        {
            // If we get HttpStatusCodeException we send the message to DLQ so the admin
            // can view the message and investigate the result
            logger.error("Could not post to Solr: [{}],  sending to DLQ.[%s] queue", logText, core);
            getJmsTemplate().convertAndSend(destinationName, entity.getBody());
        }
        // If we catch RestClientException we will propagate the error to crash the whole transaction
        // and message to stay in the main queue(ex:solrAdvancedSearch.in)
        catch (RestClientException e)
        {
            throw new SolrPostException(String.format("Could not post [%s] to Solr: %s", logText, e.getMessage()), e);
        }
    }

    /**
     * Is the exception such that we should send it to DLQ?
     * 
     * @param status
     * @return
     */
    private boolean isUnRecoverable(HttpStatus status)
    {
        return status.is4xxClientError() || status.is5xxServerError();
    }

    public SolrConfig getSolrConfig()
    {
        return solrConfig;
    }

    public void setSolrConfig(SolrConfig solrConfig)
    {
        this.solrConfig = solrConfig;
    }

    public JmsTemplate getJmsTemplate()
    {
        return jmsTemplate;
    }

    public void setJmsTemplate(JmsTemplate jmsTemplate)
    {
        this.jmsTemplate = jmsTemplate;
    }
}
