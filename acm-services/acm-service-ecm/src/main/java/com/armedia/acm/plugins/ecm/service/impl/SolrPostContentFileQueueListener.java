package com.armedia.acm.plugins.ecm.service.impl;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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
import com.armedia.acm.services.search.service.solr.SolrPostClient;
import com.armedia.acm.services.search.service.solr.SolrPostException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.jms.annotation.JmsListener;

/**
 * Created by david.miller on 2018-04-04.
 */
public class SolrPostContentFileQueueListener
{
    private transient final Logger logger = LogManager.getLogger(getClass());

    private SolrPostClient solrPostClient;

    @JmsListener(destination = "solrContentFile.in", containerFactory = "jmsListenerContainerFactory")
    public void onContentFilePost(String jsonDocument)
    {
        try
        {
            logger.debug("Sending a content file to advanced search");
            getSolrPostClient().sendToSolr(SolrCore.ADVANCED_SEARCH, jsonDocument);
        }
        catch (SolrPostException e)
        {
            logger.error("Could not post content file to Solr: {}", e.getMessage(), e);
        }
    }

    public SolrPostClient getSolrPostClient()
    {
        return solrPostClient;
    }

    public void setSolrPostClient(SolrPostClient solrPostClient)
    {
        this.solrPostClient = solrPostClient;
    }

}
