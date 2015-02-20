package com.armedia.acm.services.search.service;

import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrBaseDocument;
import com.armedia.acm.services.search.model.solr.SolrDeleteDocumentByIdRequest;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.spring.SpringContextHolder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mule.api.MuleException;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * Created by armdev on 10/21/14.
 */
public class SendDocumentsToSolr
{
    private SpringContextHolder contextHolder;

    private MuleClient muleClient;

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final ObjectMapper mapper = new ObjectMapperFactory().createObjectMapper();

    public String asJsonArray(SolrBaseDocument document) throws JsonProcessingException
    {
        log.debug("Converting a document to a JSON array");
        List<SolrBaseDocument> docs = Collections.singletonList(document);
        String json = mapper.writeValueAsString(docs);
        log.debug("returning: " + json);
        return json;
    }

    public void sendSolrAdvancedSearchDocuments(List<SolrAdvancedSearchDocument> solrDocuments)
    {
        sendToJmsQueue(solrDocuments, "jms://solrAdvancedSearch.in");
    }



    public void sendSolrQuickSearchDocuments(List<SolrDocument> solrDocuments)
    {
        sendToJmsQueue(solrDocuments, "jms://solrQuickSearch.in");
    }

    public void sendSolrContentFileIndexDocuments(List<SolrAdvancedSearchDocument> solrDocuments)
    {
        if (solrDocuments !=null ) {
            for (SolrAdvancedSearchDocument doc: solrDocuments ){

                sendToJmsQueue(doc, "jms://solrContentFile.in");
            }

        }
    }

    public void sendSolrContentFileIndexDeletes(List<SolrDeleteDocumentByIdRequest> deletes)
    {
        // send separate requests, in case any of them fail, e.g. maybe a doc with this id already is not in the
        // queue.
        if ( deletes != null )
        {
            for ( SolrDeleteDocumentByIdRequest doc : deletes )
            {
                sendToJmsQueue(doc, "jms://solrContentFile.in");
            }
        }
    }

    public void sendSolrQuickSearchDeletes(List<SolrDeleteDocumentByIdRequest> deletes)
    {
        // send separate requests, in case any of them fail, e.g. maybe a doc with this id already is not in the
        // queue.
        if ( deletes != null )
        {
            for ( SolrDeleteDocumentByIdRequest doc : deletes )
            {
                sendToJmsQueue(doc, "jms://solrQuickSearch.in");
            }
        }
    }

    public void sendSolrAdvancedSearchDeletes(List<SolrDeleteDocumentByIdRequest> deletes)
    {
        log.debug("Received " + deletes.size() + " to be deleted.");
        // send separate requests, in case any of them fail, e.g. maybe a doc with this id already is not in the
        // queue.
        if ( deletes != null )
        {
            for ( SolrDeleteDocumentByIdRequest doc : deletes )
            {
                sendToJmsQueue(doc, "jms://solrAdvancedSearch.in");
            }
        }
    }

    private void sendToJmsQueue(SolrDeleteDocumentByIdRequest solrDocument, String queueName)
    {
        try
        {
            String json = mapper.writeValueAsString(solrDocument);
            if ( log.isDebugEnabled() )
            {
                log.debug("Sending JSON to SOLR: " + json);
            }

            getMuleClient().dispatch(queueName, json, null);
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

    private void sendToJmsQueue( SolrAdvancedSearchDocument solrDocument, String queueName ) {
        try {
            if ( log.isDebugEnabled() ) {
                log.debug("Sending POJO to SOLR: " + solrDocument);
            }
            getMuleClient().dispatch(queueName, solrDocument, null);
        }
        catch ( MuleException e ) {
            log.error("Could not send document to SOLR: " + e.getMessage(), e);
        }
    }

    private void sendToJmsQueue(List<? extends SolrBaseDocument> solrDocuments, String queueName)
    {
        try
        {
            String json = mapper.writeValueAsString(solrDocuments);

            getMuleClient().dispatch(queueName, json, null);
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
