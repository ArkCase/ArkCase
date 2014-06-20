package com.armedia.acm.plugins.search.web.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-search-plugin-test.xml",
        "/spring/spring-library-activemq.xml"
})
public class QuickSearchAPIControllerIT
{
    @Autowired
    @Qualifier("searchTestMuleClient")
    private MuleClient muleClient;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Test
    public void quickSearch() throws Exception
    {
        Map<String, Object> headers = new HashMap<>();
        headers.put("query", "ebmillar");
        headers.put("firstRow", "0");
        headers.put("maxRows", "10");
        headers.put("sort", "");

        MuleMessage response = muleClient.send("vm://quickSearchQuery.in", "", headers);

        log.debug("Response type: " + response.getPayload().getClass());
        log.debug("response: " + response.getPayload());

        assertEquals(String.class, response.getPayload().getClass());


    }
}
