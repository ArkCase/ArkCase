package com.armedia.acm.services.users.web.api;

import com.armedia.acm.services.users.service.group.GroupServiceImpl;
import org.apache.commons.httpclient.HttpStatus;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static junit.framework.Assert.assertEquals;
import static org.easymock.EasyMock.expect;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Created by armdev on 5/28/14.
 */
public class GetUsersByGroupAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;
    private GroupServiceImpl mockGroupService;
    private Authentication mockAuthentication;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        mockGroupService = createMock(GroupServiceImpl.class);
        mockAuthentication = createMock(Authentication.class);

        GetUsersByGroupAPIController unit = new GetUsersByGroupAPIController();
        unit.setGroupService(mockGroupService);

        mockMvc = MockMvcBuilders.standaloneSetup(unit).build();
    }

    @Test
    public void getValidUserMembersForGroupTest() throws Exception
    {
        String response = "response";
        String group = "Group1";
        String status = "VALID";
        expect(mockAuthentication.getName()).andReturn("USER");
        expect(mockGroupService.getUserMembersForGroup(group, Optional.of(status), mockAuthentication)).andReturn(response);

        replayAll();

        MvcResult result =
                mockMvc.perform(get("/api/latest/users/by-group/{group}", group)
                        .param("status", "VALID")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .principal(mockAuthentication)
                        .contentType(MediaType.APPLICATION_JSON)).andReturn();

        log.info("results: [{}]", result.getResponse().getContentAsString());

        verifyAll();

        assertEquals(HttpStatus.SC_OK, result.getResponse().getStatus());
    }

    @Test
    public void getUserMembersForGroupTest() throws Exception
    {
        String response = "response";
        String group = "Group 1";
        expect(mockAuthentication.getName()).andReturn("USER");
        expect(mockGroupService.getUserMembersForGroup(group, Optional.empty(), mockAuthentication)).andReturn(response);

        replayAll();

        MvcResult result =
                mockMvc.perform(get("/api/latest/users/by-group/{group}", group)
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .principal(mockAuthentication)
                        .contentType(MediaType.APPLICATION_JSON)).andReturn();

        log.info("results: [{}]", result.getResponse().getContentAsString());

        verifyAll();

        assertEquals(HttpStatus.SC_OK, result.getResponse().getStatus());
    }
}
