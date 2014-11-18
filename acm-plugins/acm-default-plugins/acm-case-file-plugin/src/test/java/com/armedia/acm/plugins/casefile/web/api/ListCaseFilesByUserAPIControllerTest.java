package com.armedia.acm.plugins.casefile.web.api;

import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.utility.CaseFileEventUtility;
import com.armedia.acm.plugins.casefile.web.api.ListCaseFilesByUserAPIController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.easymock.IAnswer;
import org.easymock.IExpectationSetters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Created by marjan.stefanoski on 10/13/2014.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-case-plugin-test.xml"
})

public class ListCaseFilesByUserAPIControllerTest extends EasyMockSupport {

    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;

    private ListCaseFilesByUserAPIController unit;

    private CaseFileDao mockCaseFileDao;
    private CaseFileEventUtility mockCaseFileEventUtility;
    private Authentication mockAuthentication;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        mockCaseFileDao = createMock(CaseFileDao.class);
        mockCaseFileEventUtility = createMock(CaseFileEventUtility.class);
        mockHttpSession = new MockHttpSession();
        mockAuthentication = createMock(Authentication.class);

        unit = new ListCaseFilesByUserAPIController();

        unit.setCaseFileDao(mockCaseFileDao);
        unit.setCaseFileEventUtility(mockCaseFileEventUtility);

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();
    }

    @Test
    public void caseFilesByUserTest()  throws Exception  {

        String user = "user";

        CaseFile caseFile = new CaseFile();
        caseFile.setId(5L);
        caseFile.setCreator(user);
        caseFile.setCreated(new Date());
        String ipAddress = "ipAddress";




        expect(mockCaseFileDao.getCaseFilesByUser(user)).andReturn(Arrays.asList(caseFile));

        Capture<CaseFile> capturedCase = new Capture<CaseFile>();

        mockCaseFileEventUtility.raiseEvent(capture(capturedCase), eq("search"), anyObject(Date.class), eq(ipAddress), eq(user), eq(mockAuthentication));

        mockHttpSession.setAttribute("acm_ip_address", ipAddress);

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/latest/plugin/casefile/forUser/{user}",user)
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .principal(mockAuthentication)
                        .session(mockHttpSession))
                .andReturn();
        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        String returned = result.getResponse().getContentAsString();

        log.info("results: " + returned);

        ObjectMapper objectMapper = new ObjectMapper();

        List<CaseFile> foundCases = objectMapper.readValue(returned,
                objectMapper.getTypeFactory().constructParametricType(List.class, CaseFile.class));

        assertEquals(1, foundCases.size());

        CaseFile found = foundCases.get(0);
        assertEquals(caseFile.getId(), found.getId());
    }


}