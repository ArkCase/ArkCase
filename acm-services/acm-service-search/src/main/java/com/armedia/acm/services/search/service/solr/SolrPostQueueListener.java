package com.armedia.acm.services.search.service.solr;

import com.armedia.acm.services.search.model.SolrCore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;

/**
 * Created by david.miller on 2018-04-02.
 */
public class SolrPostQueueListener
{
    private transient final Logger logger = LoggerFactory.getLogger(getClass());

    private SolrPostClient solrPostClient;

    @JmsListener(destination = "solrAdvancedSearch.in", containerFactory = "jmsListenerContainerFactory")
    public void onAdvancedSearchPost(String jsonDocument)
    {
        try
        {
            logger.debug("Sending to advanced search");
            getSolrPostClient().sendToSolr(SolrCore.ADVANCED_SEARCH, jsonDocument);
        }
        catch (SolrPostException e)
        {
            logger.error("Could not post to Solr: {}", e.getMessage(), e);
        }
    }

    @JmsListener(destination = "solrQuickSearch.in", containerFactory = "jmsListenerContainerFactory")
    public void onQuickSearchPost(String jsonDocument)
    {
        try
        {
            logger.debug("Sending to quick search");
            getSolrPostClient().sendToSolr(SolrCore.QUICK_SEARCH, jsonDocument);
        }
        catch (SolrPostException e)
        {
            logger.error("Could not post to Solr: {}", e.getMessage(), e);
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
