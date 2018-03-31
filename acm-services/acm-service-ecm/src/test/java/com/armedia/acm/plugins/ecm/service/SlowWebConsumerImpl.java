package com.armedia.acm.plugins.ecm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class SlowWebConsumerImpl implements SlowWebConsumer
{

    private RestTemplate restTemplate = new RestTemplate();
    private transient final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void sendToSolr(String payload) throws SlowConsumerException
    {
        try
        {
            ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8080/delay", String.class);
            int status = response.getStatusCode().value();
            logger.info("http status: {}", status);
            if (status >= 400)
            {
                throw new SlowConsumerException("Status code was " + status);
            }
            logger.info("response: {}", response.getBody());

        }
        catch (Exception e)
        {
            throw new SlowConsumerException(e);
        }
    }

}
