package com.armedia.acm.services.dataaccess.web.api;

import com.armedia.acm.core.query.QueryResultPageWithTotalCount;
import com.armedia.acm.services.dataaccess.dao.AcmAccessControlDefaultDao;
import com.armedia.acm.services.dataaccess.model.AcmAccessControlDefault;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import javax.persistence.QueryTimeoutException;
import java.util.Arrays;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-dataaccess-test.xml"
})
public class ListAccessControlDefaultsControllerTest extends EasyMockSupport
{
    private AcmAccessControlDefaultDao mockAccessControlDefaultDao;
    private MockMvc mockMvc;
    private Authentication mockAuthentication;
    private MockHttpSession mockHttpSession;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private ListAccessControlDefaultsController unit;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        unit = new ListAccessControlDefaultsController();

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();

        mockAuthentication = createMock(Authentication.class);
        mockAccessControlDefaultDao = createMock(AcmAccessControlDefaultDao.class);
        mockHttpSession = new MockHttpSession();

        unit.setAccessControlDefaultDao(mockAccessControlDefaultDao);
    }

    @Test
    public void accessControlDefaults_noRequestParams() throws Exception
    {
        AcmAccessControlDefault found = new AcmAccessControlDefault();
        found.setId(500L);
        List<AcmAccessControlDefault> foundList = Arrays.asList(found);

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();

        expect(mockAccessControlDefaultDao.findPage(unit.getDefaultSort(), 0, 10)).andReturn(foundList);
        expect(mockAccessControlDefaultDao.countAll()).andReturn(foundList.size());

        replayAll();

        MvcResult result = mockMvc.perform(
            get("/api/v1/plugin/dataaccess/accessControlDefaults")
                    .principal(mockAuthentication)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

        verifyAll();

        String jsonString = result.getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        QueryResultPageWithTotalCount<AcmAccessControlDefault> retval = mapper.readValue(
                jsonString,
                mapper.getTypeFactory().constructParametricType(
                        QueryResultPageWithTotalCount.class, AcmAccessControlDefault.class));
        assertEquals(foundList.size(), retval.getTotalCount());
        assertEquals(found.getId(), retval.getResultPage().get(0).getId());

        log.debug("output: " + jsonString);
    }

    @Test
    public void accessControlDefaults_pageSizeParams() throws Exception
    {
        AcmAccessControlDefault found = new AcmAccessControlDefault();
        found.setId(500L);
        List<AcmAccessControlDefault> foundList = Arrays.asList(found);

        int start = 50;
        int max = 5;

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();

        expect(mockAccessControlDefaultDao.findPage(unit.getDefaultSort(), start, max)).andReturn(foundList);
        expect(mockAccessControlDefaultDao.countAll()).andReturn(foundList.size());

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/v1/plugin/dataaccess/accessControlDefaults?start=" + start + "&n=" + max)
                        .principal(mockAuthentication)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        verifyAll();

        String jsonString = result.getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        QueryResultPageWithTotalCount<AcmAccessControlDefault> retval = mapper.readValue(
                jsonString,
                mapper.getTypeFactory().constructParametricType(
                        QueryResultPageWithTotalCount.class, AcmAccessControlDefault.class));
        assertEquals(foundList.size(), retval.getTotalCount());
        assertEquals(found.getId(), retval.getResultPage().get(0).getId());
        assertEquals(start, retval.getStartRow());
        assertEquals(max, retval.getMaxRows());

        log.debug("output: " + jsonString);
    }

    @Test
    public void accessControlDefaults_sortParams_multipleParams() throws Exception
    {
        AcmAccessControlDefault found = new AcmAccessControlDefault();
        found.setId(500L);
        List<AcmAccessControlDefault> foundList = Arrays.asList(found);

        String orderOne = "id";
        String orderTwo = "modified";
        String[] orderArray = { orderOne, orderTwo };

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();

        expect(mockAccessControlDefaultDao.findPage(aryEq(orderArray), eq(0), eq(10))).andReturn(foundList);
        expect(mockAccessControlDefaultDao.countAll()).andReturn(foundList.size());

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/v1/plugin/dataaccess/accessControlDefaults?s=" + orderOne + "&s=" + orderTwo)
                        .principal(mockAuthentication)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        verifyAll();

        String jsonString = result.getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        QueryResultPageWithTotalCount<AcmAccessControlDefault> retval = mapper.readValue(
                jsonString,
                mapper.getTypeFactory().constructParametricType(
                        QueryResultPageWithTotalCount.class, AcmAccessControlDefault.class));
        assertEquals(foundList.size(), retval.getTotalCount());
        assertEquals(found.getId(), retval.getResultPage().get(0).getId());
        assertEquals(0, retval.getStartRow());
        assertEquals(10, retval.getMaxRows());

        log.debug("output: " + jsonString);
    }

    @Test
    public void accessControlDefaults_sortParams_oneCsvParam() throws Exception
    {
        AcmAccessControlDefault found = new AcmAccessControlDefault();
        found.setId(500L);
        List<AcmAccessControlDefault> foundList = Arrays.asList(found);

        String orderOne = "id";
        String orderTwo = "modified";
        String[] orderArray = { orderOne, orderTwo };

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();

        expect(mockAccessControlDefaultDao.findPage(aryEq(orderArray), eq(0), eq(10))).andReturn(foundList);
        expect(mockAccessControlDefaultDao.countAll()).andReturn(foundList.size());

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/v1/plugin/dataaccess/accessControlDefaults?s=" + orderOne + "," + orderTwo)
                        .principal(mockAuthentication)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        verifyAll();

        String jsonString = result.getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        QueryResultPageWithTotalCount<AcmAccessControlDefault> retval = mapper.readValue(
                jsonString,
                mapper.getTypeFactory().constructParametricType(
                        QueryResultPageWithTotalCount.class, AcmAccessControlDefault.class));
        assertEquals(foundList.size(), retval.getTotalCount());
        assertEquals(found.getId(), retval.getResultPage().get(0).getId());
        assertEquals(0, retval.getStartRow());
        assertEquals(10, retval.getMaxRows());

        log.debug("output: " + jsonString);
    }

    @Test
    public void retrieveListOfComplaints_exception() throws Exception
    {

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        expect(mockAccessControlDefaultDao.findPage(unit.getDefaultSort(), 0, 10)).andThrow(new QueryTimeoutException("testException"));

        mockHttpSession.setAttribute("acm_ip_address", "acm_ip_address");

        replayAll();

        mockMvc.perform(
                get("/api/v1/plugin/dataaccess/accessControlDefaults")
                        .principal(mockAuthentication)
                        .session(mockHttpSession))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN));

        verifyAll();

    }


}
