package com.armedia.acm.plugins.ecm.service.impl;

import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.solr.SolrPostClient;
import com.armedia.acm.services.search.service.solr.SolrPostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;

/**
 * Created by david.miller on 2018-04-04.
 */
public class SolrPostContentFileQueueListener
{
    private transient final Logger logger = LoggerFactory.getLogger(getClass());

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
