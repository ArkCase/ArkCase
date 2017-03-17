package com.armedia.acm.services.search.service;

import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrBaseDocument;
import com.armedia.acm.services.search.model.solr.SolrDeleteDocumentByIdRequest;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by armdev on 10/21/14.
 */
public class SendDocumentsToSolr
{
    private MuleContextManager muleContextManager;

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final ObjectMapper mapper = new ObjectMapperFactory().createObjectMapper();

    // this method is used from Mule, do not delete it!
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
        if (solrDocuments != null)
        {
            for (SolrAdvancedSearchDocument doc : solrDocuments)
            {

                sendToJmsQueue(doc, "jms://solrContentFile.in");
            }

        }
    }

    public void sendSolrContentFileIndexDeletes(List<SolrDeleteDocumentByIdRequest> deletes)
    {
        // send separate requests, in case any of them fail, e.g. maybe a doc with this id already is not in the
        // queue.
        if (deletes != null)
        {
            for (SolrDeleteDocumentByIdRequest doc : deletes)
            {
                sendToJmsQueue(doc, "jms://solrAdvancedSearch.in");
            }
        }
    }

    public void sendSolrQuickSearchDeletes(List<SolrDeleteDocumentByIdRequest> deletes)
    {
        // send separate requests, in case any of them fail, e.g. maybe a doc with this id already is not in the
        // queue.
        if (deletes != null)
        {
            for (SolrDeleteDocumentByIdRequest doc : deletes)
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
        if (deletes != null)
        {
            for (SolrDeleteDocumentByIdRequest doc : deletes)
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
            if (log.isDebugEnabled())
            {
                log.debug("Sending JSON to SOLR: " + json);
            }
            getMuleContextManager().dispatch(queueName, json);
            if (log.isDebugEnabled())
            {
                log.debug("Returning JSON: " + json);
            }
        } catch (JsonProcessingException | MuleException e)
        {
            log.error("Could not send document to SOLR: " + e.getMessage(), e);
        }
    }

    private void sendToJmsQueue(SolrAdvancedSearchDocument solrDocument, String queueName)
    {
        try
        {
            if (log.isDebugEnabled())
            {
                log.debug("Sending POJO to SOLR: " + solrDocument);
            }

            Map<String, Object> messageProperties = new HashMap<>();
            messageProperties.put("additionalProperties", solrDocument.getAdditionalProperties());
            getMuleContextManager().dispatch(queueName, solrDocument, messageProperties);
        } catch (MuleException e)
        {
            log.error("Could not send document to SOLR: " + e.getMessage(), e);
        }
    }

    private void sendToJmsQueue(List<? extends SolrBaseDocument> solrDocuments, String queueName)
    {
        try
        {
            String json = mapper.writeValueAsString(solrDocuments);

            getMuleContextManager().dispatch(queueName, json);
            if (log.isDebugEnabled())
            {
                log.debug("Returning JSON: " + json);
            }
        } catch (JsonProcessingException | MuleException e)
        {
            log.error("Could not send document to SOLR: " + e.getMessage(), e);
        }
    }

    public MuleContextManager getMuleContextManager()
    {
        return muleContextManager;
    }

    public void setMuleContextManager(MuleContextManager muleContextManager)
    {
        this.muleContextManager = muleContextManager;
    }
}
