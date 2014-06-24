package com.armedia.acm.services.search.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mule.api.MuleException;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.List;
import java.util.Map;

/**
 * Created by armdev on 6/24/14.
 */
public class JmsSender implements ApplicationContextAware
{

    private final Logger log = LoggerFactory.getLogger(getClass());
    private ApplicationContext applicationContext;

    public void sendToJms(List<Map<String, Object>> quickSearchDocuments)
    {
        // We have to send SOLR a JSON array.  If we send a JSON object, SOLR will interpret it as a SOLR command,
        // instead of a document to be indexed.
        ObjectMapper mapper = new ObjectMapperFactory().createObjectMapper();
        try
        {
            String json = mapper.writeValueAsString(quickSearchDocuments);

            getMuleClient().dispatch(SearchConstants.QUICK_SEARCH_JMS_QUEUE_NAME, json, null);
        }
        catch (JsonProcessingException | MuleException e)
        {
            log.error("Could not send task to SOLR: " + e.getMessage(), e);
        }
    }


    public MuleClient getMuleClient()
    {
        return getApplicationContext().getBean("muleClient", MuleClient.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext()
    {
        return applicationContext;
    }
}
