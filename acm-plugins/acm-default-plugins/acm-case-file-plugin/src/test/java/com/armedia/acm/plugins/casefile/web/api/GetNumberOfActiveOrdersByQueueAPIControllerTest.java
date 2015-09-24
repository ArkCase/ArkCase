package com.armedia.acm.plugins.casefile.web.api;

import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Created by riste.tutureski on 9/22/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-library-case-plugin-test.xml"
})
public class GetNumberOfActiveOrdersByQueueAPIControllerTest extends EasyMockSupport
{
    private Logger LOG = LoggerFactory.getLogger(getClass());

    private MockMvc mockMvc;
    private GetNumberOfActiveOrdersByQueueAPIController unit;
    private Authentication mockAuthentication;
    private CaseFileDao mockCaseFileDao;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    @Before
    public void setUp() throws Exception
    {
        unit = new GetNumberOfActiveOrdersByQueueAPIController();

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();

        mockAuthentication = createMock(Authentication.class);
        mockCaseFileDao = createMock(CaseFileDao.class);

        unit.setCaseFileDao(mockCaseFileDao);
    }

    @Test
    public void testNumberOfActiveOrdersByQueue() throws Exception
    {
        Map<String, Long> expectedResult = new LinkedHashMap<>();
        expectedResult.put("Queue1", 5L);
        expectedResult.put("Queue2", 3L);
        expectedResult.put("Queue3", 0L);
        expectedResult.put("Queue4", 1L);
        expectedResult.put("Queue5", 8L);

        expect(mockAuthentication.getName()).andReturn("user");
        expect(mockCaseFileDao.getNumberOfActiveOrdersByQueue()).andReturn(expectedResult);

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/latest/plugin/casefile/number/by/queue")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockAuthentication)
        ).andReturn();

        verifyAll();

        LOG.info("Results: " + result.getResponse().getContentAsString());

        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Long> resultMap = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<Map<String, Long>>() {});

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals(5, resultMap.size());

        assertEquals(expectedResult.keySet().toArray()[0], resultMap.keySet().toArray()[0]);
        assertEquals(expectedResult.keySet().toArray()[1], resultMap.keySet().toArray()[1]);
        assertEquals(expectedResult.keySet().toArray()[2], resultMap.keySet().toArray()[2]);
        assertEquals(expectedResult.keySet().toArray()[3], resultMap.keySet().toArray()[3]);
        assertEquals(expectedResult.keySet().toArray()[4], resultMap.keySet().toArray()[4]);

        assertEquals(expectedResult.entrySet().toArray()[0], resultMap.entrySet().toArray()[0]);
        assertEquals(expectedResult.entrySet().toArray()[1], resultMap.entrySet().toArray()[1]);
        assertEquals(expectedResult.entrySet().toArray()[2], resultMap.entrySet().toArray()[2]);
        assertEquals(expectedResult.entrySet().toArray()[3], resultMap.entrySet().toArray()[3]);
        assertEquals(expectedResult.entrySet().toArray()[4], resultMap.entrySet().toArray()[4]);
    }
}
