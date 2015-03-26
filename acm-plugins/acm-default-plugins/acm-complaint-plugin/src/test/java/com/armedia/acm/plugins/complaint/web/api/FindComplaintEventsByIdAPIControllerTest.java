package com.armedia.acm.plugins.complaint.web.api;

import java.util.Arrays;
import java.util.Date;

import com.armedia.acm.audit.dao.AuditDao;
import com.armedia.acm.audit.model.AuditEvent;
import com.armedia.acm.core.query.QueryResultPageWithTotalCount;

import org.codehaus.jackson.map.ObjectMapper;
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

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-complaint-plugin-test.xml"
})
public class FindComplaintEventsByIdAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;

    private FindComplaintEventsByIdAPIController unit;

    private AuditDao mockAuditDao;
    private Authentication mockAuthentication;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        mockAuditDao = createMock(AuditDao.class);
        mockHttpSession = new MockHttpSession();
        mockAuthentication = createMock(Authentication.class);

        unit = new FindComplaintEventsByIdAPIController();

        unit.setAuditDao(mockAuditDao);

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();
    }

    @Test
    public void findComplaintEventsById() throws Exception
    {
        String ipAddress = "ipAddress";
        Long complaintId = 500L;
        String objectType = "COMPLAINT";
        String eventResult = "eventResult";
        String fullEventType = "fullEventType";
        Date eventDate = new Date();

        AuditEvent auditEvent = new AuditEvent();
        
        auditEvent.setObjectId(complaintId);
        auditEvent.setObjectType(objectType);
        auditEvent.setIpAddress(ipAddress);
        auditEvent.setEventResult(eventResult);
        auditEvent.setFullEventType(fullEventType);
        auditEvent.setEventDate(eventDate);
        
        QueryResultPageWithTotalCount<AuditEvent> expected = new QueryResultPageWithTotalCount<>();
        expected.setStartRow(0);
        expected.setMaxRows(10);
        expected.setTotalCount(1);
        expected.setResultPage(Arrays.asList(auditEvent));

        mockHttpSession.setAttribute("acm_ip_address", ipAddress);

        expect(mockAuditDao.findPagedResults(complaintId, objectType, 0, 10)).andReturn(Arrays.asList(auditEvent));
        expect(mockAuditDao.countAll(complaintId, objectType)).andReturn(1);

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/latest/plugin/complaint/events/{complaintId}", complaintId)
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .session(mockHttpSession)
                        .principal(mockAuthentication))
                .andReturn();

        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        String jsonResult = result.getResponse().getContentAsString();

        log.info("results: " + jsonResult);
        
        String jsonExpected = new ObjectMapper().writeValueAsString(expected);
        
        assertEquals(jsonExpected, jsonResult);
    }

}
