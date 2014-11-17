package com.armedia.acm.services.search.web.api;

import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import com.armedia.acm.services.search.model.ApplicationSearchEvent;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.model.solr.SolrResponse;
import com.armedia.acm.services.search.service.SearchEventPublisher;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-library-search-web-api-test.xml"
})
public class SearchObjectByTypeAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;
    private Authentication mockAuthentication;
    private MockHttpSession mockHttpSession;
    
    private MuleClient mockMuleClient;
    private MuleMessage mockMuleMessage;
    private SearchEventPublisher mockSearchEventPublisher;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private SearchObjectByTypeAPIController unit;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        unit = new SearchObjectByTypeAPIController();

        mockMuleClient = createMock(MuleClient.class);
        mockMuleMessage = createMock(MuleMessage.class);
        mockSearchEventPublisher = createMock(SearchEventPublisher.class);

        unit.setMuleClient(mockMuleClient);
        unit.setSearchEventPublisher(mockSearchEventPublisher);

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();

        mockAuthentication = createMock(Authentication.class);
        mockHttpSession = new MockHttpSession();
    }
    
    @Test
    public void jsonPayload() throws Exception {
        // there are docs
        String jsonPayload = "{\"responseHeader\":{\"status\":0,\"QTime\":3,\"params\":{\"sort\":\"\",\"indent\":\"true\",\"start\":\"0\",\"q\":\"object_type_s:Complaint\",\"wt\":\"json\",\"rows\":\"10\"}},\"response\":{\"numFound\":5,\"start\":0,\"docs\":[{\"id\":\"142-Complaint\",\"status_s\":\"DRAFT\",\"author\":\"tester\",\"author_s\":\"tester\",\"modifier_s\":\"testModifier\",\"last_modified\":\"2014-08-15T17:13:55Z\",\"create_dt\":\"2014-08-15T17:13:55Z\",\"title_t\":\"testTitle\",\"name\":\"20140815_142\",\"object_id_s\":\"142\",\"owner_s\":\"tester\",\"object_type_s\":\"Complaint\",\"_version_\":1477062417430085632},{\"id\":\"159-Complaint\",\"status_s\":\"DRAFT\",\"author\":\"tester\",\"author_s\":\"tester\",\"modifier_s\":\"testModifier\",\"last_modified\":\"2014-08-18T11:51:09Z\",\"create_dt\":\"2014-08-18T11:51:09Z\",\"title_t\":\"testTitle\",\"name\":\"20140818_159\",\"object_id_s\":\"159\",\"owner_s\":\"tester\",\"object_type_s\":\"Complaint\",\"_version_\":1477062417682792448},{\"status_s\":\"DRAFT\",\"create_dt\":\"2014-08-22T09:27:41Z\",\"title_t\":\"First Complaint\",\"object_id_s\":\"130\",\"owner_s\":\"ann-acm\",\"deny_acl_ss\":[\"TEST-DENY-ACL\"],\"object_type_s\":\"Complaint\",\"allow_acl_ss\":[\"TEST-ALLOW-ACL\"],\"id\":\"130-Complaint\",\"modifier_s\":\"ann-acm\",\"author\":\"ann-acm\",\"author_s\":\"ann-acm\",\"last_modified\":\"2014-08-22T09:27:41Z\",\"name\":\"20140822_130\",\"_version_\":1478712820530937856},{\"status_s\":\"DRAFT\",\"create_dt\":\"2014-09-15T09:16:04Z\",\"title_t\":\"Monday Sept 15\",\"object_id_s\":\"270\",\"owner_s\":\"ann-acm\",\"deny_acl_ss\":[\"TEST-DENY-ACL\"],\"object_type_s\":\"Complaint\",\"allow_acl_ss\":[\"TEST-ALLOW-ACL\"],\"id\":\"270-Complaint\",\"modifier_s\":\"ann-acm\",\"author\":\"ann-acm\",\"author_s\":\"ann-acm\",\"last_modified\":\"2014-09-15T09:16:04Z\",\"name\":\"20140905_001\",\"_version_\":1479317404993454080},{\"status_s\":\"DRAFT\",\"create_dt\":\"2014-09-15T09:33:01Z\",\"title_t\":\"Monday Sept 15 2\",\"object_id_s\":\"275\",\"owner_s\":\"ann-acm\",\"deny_acl_ss\":[\"TEST-DENY-ACL\"],\"object_type_s\":\"Complaint\",\"allow_acl_ss\":[\"TEST-ALLOW-ACL\"],\"id\":\"275-Complaint\",\"modifier_s\":\"ann-acm\",\"author\":\"ann-acm\",\"author_s\":\"ann-acm\",\"last_modified\":\"2014-09-15T09:33:01Z\",\"name\":\"20140905_002\",\"_version_\":1479318645547991040}]}}";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        SolrResponse solrResponse = gson.fromJson(jsonPayload, SolrResponse.class);
       
        List<SolrDocument> solrDocs = solrResponse.getResponse().getDocs();
        
        assertTrue(solrDocs.size() > 0);
        
        // no docs response
        jsonPayload = "{\"responseHeader\":{\"status\":400,\"QTime\":5,\"params\":{\"sort\":\"\",\"indent\":\"true\",\"start\":\"0\",\"q\":\"object_type_:NOTYPE\",\"wt\":\"json\",\"rows\":\"10\"}},\"error\":{\"msg\":\"undefined field object_type_\",\"code\":400}}";
        solrResponse = gson.fromJson(jsonPayload, SolrResponse.class);
        assertNull(solrResponse.getResponse());
    }

    @Test
    public void searchObjectByType_oneDocumentFound() throws Exception
    {
        String objectType = "COMPLAINT";
        int firstRow = 0;
        int maxRows = 10;
        String sort = "";
        
        String query = "object_type_s:" + objectType + " AND -status_s:COMPLETE AND -status_s:DELETE";
                   
        String solrResponse = "{\"responseHeader\":{\"status\":0,\"QTime\":3,\"params\":{\"sort\":\"\",\"indent\":\"true\",\"start\":\"0\",\"q\":\"object_type_s:Complaint\",\"wt\":\"json\",\"rows\":\"10\"}},\"response\":{\"numFound\":5,\"start\":0,\"docs\":[{\"id\":\"142-Complaint\",\"status_s\":\"DRAFT\",\"author\":\"tester\",\"author_s\":\"tester\",\"modifier_s\":\"testModifier\",\"last_modified\":\"2014-08-15T17:13:55Z\",\"create_dt\":\"2014-08-15T17:13:55Z\",\"title_t\":\"testTitle\",\"name\":\"20140815_142\",\"object_id_s\":\"142\",\"owner_s\":\"tester\",\"object_type_s\":\"Complaint\",\"_version_\":1477062417430085632}]}}";

        Map<String, Object> headers = new HashMap<>();
        headers.put("query", query);
        headers.put("firstRow", firstRow);
        headers.put("maxRows", maxRows);
        headers.put("sort", sort);
        
        Capture<ApplicationSearchEvent> capturedEvent = new Capture<>();
      
        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();
        expect(mockMuleClient.send("vm://quickSearchQuery.in", "", headers)).andReturn(mockMuleMessage);
        expect(mockMuleMessage.getPayload()).andReturn(solrResponse).atLeastOnce();
        mockSearchEventPublisher.publishSearchEvent(capture(capturedEvent));

        replayAll();
        
//		// To see details on the HTTP calls, change .andReturn() to .andDo(print())	
//		ResultActions resultAction = mockMvc.perform(
//                get("/api/v1/plugin/search/{objectType}", objectType)  
//                .principal(mockAuthentication)).andDo(print());
		
		
        MvcResult result = mockMvc.perform(
                get("/api/v1/plugin/search/{objectType}", objectType) 
                .session(mockHttpSession)
                .principal(mockAuthentication))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        verifyAll();

        String jsonString = result.getResponse().getContentAsString();

        log.debug("Got JSON: " + jsonString);

        assertEquals(solrResponse, jsonString);


    }

    @Test
    public void searchObjectByType_exception() throws Exception
    {
        String objectType = "COMPLAINT";
        int firstRow = 0;
        int maxRows = 10;
        String sort = "";
        
        String query = "object_type_s:" + objectType + " AND -status_s:COMPLETE AND -status_s:DELETE";
                   
        String solrResponse = "{ \"solrResponse\": \"this is a test response.\" }";

        Map<String, Object> headers = new HashMap<>();
        headers.put("query", query);
        headers.put("firstRow", firstRow);
        headers.put("maxRows", maxRows);
        headers.put("sort", sort);
      
        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();
        expect(mockMuleClient.send("vm://quickSearchQuery.in", "", headers)).andReturn(mockMuleMessage);
        expect(mockMuleMessage.getPayload()).andReturn(solrResponse).atLeastOnce();
        //expect(mockMuleMessage.getPayload()).andThrow(new RuntimeException());
        
        replayAll();

		// To see details on the HTTP calls, change .andReturn() to .andDo(print())	
		ResultActions resultAction = mockMvc.perform(
                get("/api/v1/plugin/search/{objectType}", objectType)  
                .session(mockHttpSession)
                .principal(mockAuthentication)).andDo(print());
        
//        MvcResult result = mockMvc.perform(
//                get("/api/v1/plugin/search/{objectType}", objectType)  
//                .session(mockHttpSession)
//                .principal(mockAuthentication))
//                .andExpect(status().isInternalServerError())
//                .andExpect(content().contentType(MediaType.TEXT_PLAIN))
//                .andReturn();

        verifyAll();

        //assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }

}
