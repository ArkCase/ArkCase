package com.armedia.acm.services.search.service;

import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrBaseDocument;
import com.armedia.acm.services.search.model.solr.SolrContentDocument;
import com.armedia.acm.services.search.model.solr.SolrDeleteDocumentByIdRequest;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.fasterxml.jackson.core.JsonProcessingException;

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
    private ObjectConverter objectConverter;

    private final Logger log = LoggerFactory.getLogger(getClass());

    // this method is used from Mule, do not delete it!
    public String asJsonArray(SolrBaseDocument document) throws JsonProcessingException
    {
        log.trace("Converting a document to a JSON array");
        List<SolrBaseDocument> docs = Collections.singletonList(document);
        String json = objectConverter.getJsonMarshaller().marshal(docs);
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

    public void sendSolrContentFileIndexDocuments(List<SolrContentDocument> solrDocuments)
    {
        if (solrDocuments != null)
        {
            for (SolrContentDocument doc : solrDocuments)
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
        log.debug("Received [{}] to be deleted.", deletes.size());
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
            String json = objectConverter.getJsonMarshaller().marshal(solrDocument);

            log.debug("Sending JSON to SOLR with hash {}", json.hashCode());

            getMuleContextManager().dispatch(queueName, json);
            log.debug("Sent JSON to SOLR with hash {}", json.hashCode());

            log.trace("Returning JSON: {}", json);

        }
        catch (MuleException e)
        {
            log.error("Could not send document to SOLR: " + e.getMessage(), e);
        }
    }

    private void sendToJmsQueue(SolrContentDocument solrDocument, String queueName)
    {
        try
        {
            log.trace("Sending POJO to SOLR: {}", solrDocument);

            Map<String, Object> messageProperties = new HashMap<>();
            messageProperties.put("additionalProperties", solrDocument.getAdditionalProperties());
            messageProperties.put("url", solrDocument.getUrl());

            log.debug("Sending a doc to Solr with hash {}", solrDocument.hashCode());
            getMuleContextManager().dispatch(queueName, solrDocument, messageProperties);
            log.debug("Sent a doc to Solr with hash {}", solrDocument.hashCode());
        }
        catch (MuleException e)
        {
            log.error("Could not send document to SOLR: " + e.getMessage(), e);
        }
    }

    private void sendToJmsQueue(List<? extends SolrBaseDocument> solrDocuments, String queueName)
    {
        try
        {
            String json = objectConverter.getJsonMarshaller().marshal(solrDocuments);

            log.debug("Sending json to Solr via JMS with hash {}", json.hashCode());
            getMuleContextManager().dispatch(queueName, json);
            log.debug("Sent json to Solr via JMS with hash {}", json.hashCode());

            log.trace("Returning JSON: {}", json);

        }
        catch (MuleException e)
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

    public ObjectConverter getObjectConverter()
    {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
    }
}
