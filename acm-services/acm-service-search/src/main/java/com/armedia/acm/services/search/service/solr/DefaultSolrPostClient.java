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

import com.armedia.acm.services.search.model.SolrCore;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Objects;

public class DefaultSolrPostClient implements SolrPostClient
{

    private transient final Logger logger = LogManager.getLogger(getClass());
    private SolrRestClient solrRestClient;

    @Override
    public void sendToSolr(SolrCore core, String json) throws SolrPostException
    {
        Objects.requireNonNull(core, "Core must be specified");
        Objects.requireNonNull(json, "JSON must be specified");
        Objects.requireNonNull(core.getCore(), "The Solr core must have a value");

        String logJson = json.length() > 50 ? json.substring(0, 50) + "..." : json;
        logger.debug("Sending to Solr core {} with JSON {}", core.getCore(), logJson);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        getSolrRestClient().postToSolr(core.getCore(), solrRestClient.getSolrConfig().getUpdateHandler(), entity, logJson);
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
