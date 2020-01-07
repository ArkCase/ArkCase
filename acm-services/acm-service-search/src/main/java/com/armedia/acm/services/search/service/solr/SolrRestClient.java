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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class SolrRestClient
{

    private transient final RestTemplate restTemplate = new RestTemplate();
    private transient final Logger logger = LogManager.getLogger(getClass());
    private SolrConfig solrConfig;

    public void postToSolr(String core, String contentHandler, HttpEntity<InputStreamResource> entity, String logText,
            String urlWithPlaceholders, Map<String, Object> urlValues)
            throws SolrPostException
    {
        String url = String.format("https://%s:%s/%s/%s/%s?overwrite=true&%s",
                solrConfig.getHost(), solrConfig.getPort(), solrConfig.getContextRoot(), core, contentHandler, urlWithPlaceholders);

        try
        {
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class, urlValues);
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

    public void postToSolr(String core, String contentHandler, HttpEntity<? extends Object> entity, String logText)
            throws SolrPostException
    {
        String url = String.format("https://%s:%s/%s/%s/%s?overwrite=true",
                solrConfig.getHost(), solrConfig.getPort(), solrConfig.getContextRoot(), core, contentHandler);

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
     * @param status
     * @return
     */
    private boolean isRecoverable(HttpStatus status)
    {
        return status.is5xxServerError()
                || HttpStatus.NOT_FOUND.equals(status)
                || HttpStatus.CONFLICT.equals(status)
                || HttpStatus.TOO_MANY_REQUESTS.equals(status);
    }

    public SolrConfig getSolrConfig()
    {
        return solrConfig;
    }

    public void setSolrConfig(SolrConfig solrConfig)
    {
        this.solrConfig = solrConfig;
    }
}
