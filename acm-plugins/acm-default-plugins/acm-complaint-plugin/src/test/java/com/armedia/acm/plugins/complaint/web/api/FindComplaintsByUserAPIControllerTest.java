package com.armedia.acm.plugins.complaint.web.api;

import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.ComplaintListView;
import com.armedia.acm.plugins.complaint.service.ComplaintEventPublisher;
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

import javax.persistence.PersistenceException;
import java.util.Arrays;
import java.util.List;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by marjan.stefanoski on 8/20/2014.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-complaint-plugin-test.xml"
})
public class FindComplaintsByUserAPIControllerTest extends EasyMockSupport {

    private MockMvc mockMvc;
    private Authentication mockAuthentication;
    private ComplaintDao mockComplaintDao;
    private ComplaintEventPublisher mockEventPublisher;
    private MockHttpSession mockHttpSession;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private FindComplaintsByUserAPIController unit;
    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        unit = new FindComplaintsByUserAPIController();

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();

        mockAuthentication = createMock(Authentication.class);
        mockComplaintDao = createMock(ComplaintDao.class);
        mockEventPublisher = createMock(ComplaintEventPublisher.class);
        mockHttpSession = new MockHttpSession();

        unit.setComplaintDao(mockComplaintDao);
        unit.setEventPublisher(mockEventPublisher);
    }

    @Test
    public void retrieveListOfUserComplaints() throws Exception
    {

        String userId="user";
        ComplaintListView complaint = new ComplaintListView();

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        expect(mockComplaintDao.listAllUserComplaints(userId)).andReturn(Arrays.asList(complaint));

        mockHttpSession.setAttribute("acm_ip_address", "acm_ip_address");

        mockEventPublisher.publishComplaintSearchResultEvent(complaint, mockAuthentication, "acm_ip_address");

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/latest/plugin/complaint/forUser/{user}",userId)
                        .principal(mockAuthentication)
                        .session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        verifyAll();

        String jsonString = result.getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        List<ComplaintListView> foundComplaints = mapper.readValue(
                jsonString,
                mapper.getTypeFactory().constructParametricType(List.class, ComplaintListView.class));

        assertEquals(1, foundComplaints.size());
    }

    @Test
    public void complaintsForUser_exception() throws Exception
    {
        String user = "user";
        String ipAddress = "ipAddress";
        expect(mockComplaintDao.listAllUserComplaints(user)).andThrow(new PersistenceException("testMessage"));
        mockHttpSession.setAttribute("acm_ip_address", ipAddress);

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");
        replayAll();
        mockMvc.perform(
                get("/api/v1/plugin/complaint/forUser/{user}", user)
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .principal(mockAuthentication)
                        .session(mockHttpSession))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN));

        verifyAll();
    }
}
