package com.armedia.acm.services.users.web.api.group;

import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.group.AcmGroupStatus;
import com.armedia.acm.services.users.service.group.GroupServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mule.api.MuleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Map;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(EasyMockRunner.class)
public class AcmGroupAPIControllerTest extends EasyMockSupport implements HandlerExceptionResolver
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private MockMvc mockMvc;

    @Mock
    private GroupServiceImpl groupService;

    @Mock
    MuleMessage muleMessage;

    @Mock
    private MuleContextManager muleContextManager;

    @TestSubject
    private AcmGroupAPIController unit = new AcmGroupAPIController();

    @Mock
    private Authentication mockAuthentication;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    @Before
    public void setUp()
    {
        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(this).build();
    }

    @Test
    public void removeGroupTestTrue() throws Exception
    {
        AcmGroup group = new AcmGroup();
        group.setName("Name");
        group.setStatus(AcmGroupStatus.DELETE);

        expect(groupService.markGroupDeleted(group.getName())).andReturn(group);
        expect(mockAuthentication.getName()).andReturn("user");

        replayAll();

        MvcResult result = mockMvc.perform(
                delete("/api/v1/users/group/{groupId}/remove", Base64.getUrlEncoder().encodeToString(group.getName().getBytes()))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockAuthentication))
                .andReturn();

        log.info("Results: {}", result.getResponse().getContentAsString());

        verifyAll();

        ObjectMapper objectMapper = new ObjectMapper();
        AcmGroup resultGroup = objectMapper.readValue(result.getResponse().getContentAsString(), AcmGroup.class);

        assertEquals(AcmGroupStatus.DELETE, resultGroup.getStatus());
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    public void getSubGroupsTestTrue() throws Exception
    {
        String groupId = "some_group////_name";
        Capture<Map> groupNameCapture = EasyMock.newCapture();
        expect(mockAuthentication.getName()).andReturn("user").anyTimes();
        expect(muleContextManager.send(anyString(), eq(""), capture(groupNameCapture))).andReturn(muleMessage).anyTimes();
        expect(muleMessage.getPayload()).andReturn("{\"reponse\" : \"response\"}").anyTimes();

        replayAll();

        String encodedGroupId = Base64.getUrlEncoder().encodeToString(groupId.getBytes());

        MvcResult result = mockMvc.perform(
                get("/api/v1/users/group/" + encodedGroupId + "/get/subgroups")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockAuthentication))
                .andReturn();

        log.info("Results: {}", result.getResponse().getContentAsString());

        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

        assertTrue(groupNameCapture.getValue().get("query").toString().contains(groupId));
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o,
                                         Exception e)
    {
        log.error("An error occurred", e);
        return null;
    }
}
