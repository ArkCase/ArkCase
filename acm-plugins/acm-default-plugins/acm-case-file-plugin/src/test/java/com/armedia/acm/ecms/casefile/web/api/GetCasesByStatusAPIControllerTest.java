package com.armedia.acm.ecms.casefile.web.api;

import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseByStatusDto;
import com.armedia.acm.plugins.casefile.model.CasesByStatusAndTimePeriod;
import com.armedia.acm.plugins.casefile.model.TimePeriod;
import com.armedia.acm.plugins.casefile.utility.CaseFileEventUtility;
import com.armedia.acm.plugins.casefile.web.api.GetCasesByStatusAPIController;
import com.armedia.acm.plugins.casefile.web.api.ListCaseFilesByUserAPIController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.easymock.EasyMockSupport;
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

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
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
public class GetCasesByStatusAPIControllerTest extends EasyMockSupport {

    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;

    private GetCasesByStatusAPIController unit;

    private CaseFileDao mockCaseFileDao;
    private Authentication mockAuthentication;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        mockCaseFileDao = createMock(CaseFileDao.class);
        mockHttpSession = new MockHttpSession();
        mockAuthentication = createMock(Authentication.class);

        unit = new GetCasesByStatusAPIController();

        unit.setCaseFileDao(mockCaseFileDao);

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();
    }
    @Test
    public  void lastYearCaseByStatusTest()  throws Exception  {
        String user = "user";

        CaseByStatusDto caseByStatusDto = new CaseByStatusDto();
        caseByStatusDto.setCount(34);
        caseByStatusDto.setStatus("TEST STATUS");
        String ipAddress = "ipAddress";


        expect(mockCaseFileDao.getCasesByStatusAndByTimePeriod(TimePeriod.ONE_YEAR)).andReturn(Arrays.asList(caseByStatusDto)).anyTimes();

        mockHttpSession.setAttribute("acm_ip_address", ipAddress);

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/latest/plugin/casebystatus/{timePeriod}", CasesByStatusAndTimePeriod.LAST_YEAR.getPeriod())
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

        List<CaseByStatusDto> caseByStatusDtos = objectMapper.readValue(returned,
                objectMapper.getTypeFactory().constructParametricType(List.class, CaseByStatusDto.class));

        assertEquals(1, caseByStatusDtos.size());

        CaseByStatusDto found = caseByStatusDtos.get(0);
        assertEquals(caseByStatusDto.getCount(), found.getCount());
    }

    @Test
    public  void lastMonthCaseByStatusTest()  throws Exception  {
        String user = "user";
        CaseByStatusDto caseByStatusDto = new CaseByStatusDto();
        caseByStatusDto.setCount(34);
        caseByStatusDto.setStatus("TEST STATUS");
        String ipAddress = "ipAddress";


        expect(mockCaseFileDao.getCasesByStatusAndByTimePeriod(TimePeriod.THIRTY_DAYS)).andReturn(Arrays.asList(caseByStatusDto)).anyTimes();

        mockHttpSession.setAttribute("acm_ip_address", ipAddress);

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/latest/plugin/casebystatus/{timePeriod}", CasesByStatusAndTimePeriod.LAST_MONTH.getPeriod())
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

        List<CaseByStatusDto> caseByStatusDtos = objectMapper.readValue(returned,
                objectMapper.getTypeFactory().constructParametricType(List.class, CaseByStatusDto.class));

        assertEquals(1, caseByStatusDtos.size());

        CaseByStatusDto found = caseByStatusDtos.get(0);
        assertEquals(caseByStatusDto.getCount(), found.getCount());
    }

    @Test
    public  void lastSevenDaysCaseByStatusTest()  throws Exception  {

        String user = "user";
        CaseByStatusDto caseByStatusDto = new CaseByStatusDto();
        caseByStatusDto.setCount(34);
        caseByStatusDto.setStatus("TEST STATUS");
        String ipAddress = "ipAddress";


        expect(mockCaseFileDao.getCasesByStatusAndByTimePeriod(TimePeriod.SEVEN_DAYS)).andReturn(Arrays.asList(caseByStatusDto)).anyTimes();

        mockHttpSession.setAttribute("acm_ip_address", ipAddress);

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/latest/plugin/casebystatus/{timePeriod}", CasesByStatusAndTimePeriod.LAST_WEEK.getPeriod())
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

        List<CaseByStatusDto> caseByStatusDtos = objectMapper.readValue(returned,
                objectMapper.getTypeFactory().constructParametricType(List.class, CaseByStatusDto.class));

        assertEquals(1, caseByStatusDtos.size());

        CaseByStatusDto found = caseByStatusDtos.get(0);
        assertEquals(caseByStatusDto.getCount(), found.getCount());
    }

    @Test
    public void allCaseByStatusTest() throws Exception
    {
        String user = "user";
        CaseByStatusDto caseByStatusDto = new CaseByStatusDto();
        caseByStatusDto.setCount(34);
        caseByStatusDto.setStatus("TEST STATUS");
        String ipAddress = "ipAddress";


        expect(mockCaseFileDao.getAllCasesByStatus()).andReturn(Arrays.asList(caseByStatusDto));

        mockHttpSession.setAttribute("acm_ip_address", ipAddress);

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/latest/plugin/casebystatus/{timePeriod}", "all")
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

        List<CaseByStatusDto> caseByStatusDtos = objectMapper.readValue(returned,
                objectMapper.getTypeFactory().constructParametricType(List.class, CaseByStatusDto.class));

        assertEquals(1, caseByStatusDtos.size());

        CaseByStatusDto found = caseByStatusDtos.get(0);
        assertEquals(caseByStatusDto.getCount(), found.getCount());
    }
}
