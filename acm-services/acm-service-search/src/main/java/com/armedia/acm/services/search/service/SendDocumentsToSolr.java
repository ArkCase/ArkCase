package com.armedia.acm.services.search.service;

import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.spring.SpringContextHolder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mule.api.MuleException;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

/**
 * Created by armdev on 10/21/14.
 */
public class SendDocumentsToSolr
{
    private SpringContextHolder contextHolder;

    private MuleClient muleClient;

    private final Logger log = LoggerFactory.getLogger(getClass());

    public void sendSolrDocument(SolrAdvancedSearchDocument solrDocument)
    {

        ObjectMapper mapper = new ObjectMapperFactory().createObjectMapper();

        String json = null;
        try
        {
            json = mapper.writeValueAsString(Collections.singletonList(solrDocument));

            getMuleClient().dispatch("jms://solrAdvancedSearch.in", json, null);
            if ( log.isDebugEnabled() )
            {
                log.debug("Returning JSON: " + json);
            }
        }
        catch (JsonProcessingException | MuleException e)
        {
            log.error("Could not send document to SOLR: " + e.getMessage(), e);
        }


    }


    public synchronized MuleClient getMuleClient()
    {
        if ( muleClient == null )
        {
            muleClient = getContextHolder().getAllBeansOfType(MuleClient.class).values().iterator().next();
        }

        return muleClient;
    }


    public SpringContextHolder getContextHolder()
    {
        return contextHolder;
    }

    public void setContextHolder(SpringContextHolder contextHolder)
    {
        this.contextHolder = contextHolder;
    }
}
