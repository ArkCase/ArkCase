package com.armedia.acm.services.users.web.api.group;

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.service.group.GroupServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(EasyMockRunner.class)
public class SupervisorGroupAPIControllerTest extends EasyMockSupport implements HandlerExceptionResolver
{

    private Logger log = LoggerFactory.getLogger(getClass());

    @TestSubject
    private SupervisorGroupAPIController unit = new SupervisorGroupAPIController();

    private MockMvc mockMvc;

    @Mock
    private Authentication mockAuthentication;

    @Mock
    private GroupServiceImpl groupService;

    @Before
    public void setUp()
    {
        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(this).build();
    }

    @Test
    public void removeSupervisorFromGroupTest() throws Exception
    {
        AcmGroup group = new AcmGroup();
        group.setName("A");

        expect(groupService.removeSupervisor("A", false)).andReturn(group);
        expect(mockAuthentication.getName()).andReturn("ann-acm");
        replayAll();

        MvcResult result = mockMvc.perform(
                delete("/api/latest/users/group/A/supervisor/remove/false")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockAuthentication))
                .andReturn();

        log.info("Results: {}", result.getResponse().getContentAsString());

        verifyAll();

        ObjectMapper objectMapper = new ObjectMapper();
        AcmGroup resultGroup = objectMapper.readValue(result.getResponse().getContentAsString(), AcmGroup.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals(null, resultGroup.getSupervisor());
    }

    @Test
    public void addSupervisorToGroupTest() throws Exception
    {
        String content = "{\"userId\":\"ann-acm\"}";

        AcmUser supervisor = new AcmUser();
        supervisor.setUserId("ann-acm");

        AcmGroup group = new AcmGroup();
        group.setName("A");
        group.setSupervisor(supervisor);

        expect(groupService.setSupervisor(supervisor, "A", false)).andReturn(group);
        expect(mockAuthentication.getName()).andReturn("ann-acm");
        replayAll();

        MvcResult result = mockMvc.perform(
                post("/api/latest/users/group/{groupId}/supervisor/save/false", group.getName())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockAuthentication)
                        .content(content))
                .andReturn();

        log.info("Results: {}", result.getResponse().getContentAsString());

        verifyAll();

        ObjectMapper objectMapper = new ObjectMapper();
        AcmGroup resultGroup = objectMapper.readValue(result.getResponse().getContentAsString(), AcmGroup.class);

        assertEquals(supervisor.getUserId(), resultGroup.getSupervisor().getUserId());
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o,
                                         Exception e)
    {
        log.error("An error occurred", e);
        return null;
    }
}
