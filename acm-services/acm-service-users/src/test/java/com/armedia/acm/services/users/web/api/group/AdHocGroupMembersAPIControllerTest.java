package com.armedia.acm.services.users.web.api.group;

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserState;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.group.AcmGroupStatus;
import com.armedia.acm.services.users.model.group.AcmGroupType;
import com.armedia.acm.services.users.service.group.GroupServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
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
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(EasyMockRunner.class)
public class AdHocGroupMembersAPIControllerTest extends EasyMockSupport implements HandlerExceptionResolver
{
    private Logger log = LoggerFactory.getLogger(getClass());

    @TestSubject
    private AdHocGroupMembersAPIController unit = new AdHocGroupMembersAPIController();

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

    private String loadFile(String fileName)
    {
        ClassLoader classLoader = getClass().getClassLoader();
        try
        {
            return IOUtils.toString(classLoader.getResourceAsStream(fileName));
        }
        catch (IOException e)
        {
            log.error("Can not load file [{}]", fileName, e);
            throw new RuntimeException(e);
        }
    }

    @Test
    public void saveMembersToAdHocGroupTest() throws Exception
    {
        String content = loadFile("data/userMembers.json");

        AcmGroup group = new AcmGroup();
        group.setName("A");
        group.setType(AcmGroupType.ADHOC_GROUP);
        group.setStatus(AcmGroupStatus.ACTIVE);

        AcmUser user = new AcmUser();
        user.setUserId("ann-acm");
        user.setUserDirectoryName("armedia");
        user.setUserState(AcmUserState.VALID);
        user.setFirstName("Ann");
        user.setLastName("Administrator");
        user.setFullName("Ann Administrator");

        group.addUserMember(user);

        expect(groupService.addMembersToAdHocGroup(new HashSet<>(Arrays.asList(user)), "A")).andReturn(group);
        expect(mockAuthentication.getName()).andReturn("ann-acm");
        replayAll();

        MvcResult result = mockMvc.perform(
                post("/api/latest/users/group/{group}/members/save", group.getName())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .principal(mockAuthentication))
                .andExpect(status().isOk())
                .andReturn();

        verifyAll();

        log.info("Results: {}", result.getResponse().getContentAsString());
        ObjectMapper om = new ObjectMapper();
        AcmGroup acmGroup = om.readValue(result.getResponse().getContentAsString(), AcmGroup.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        String[] members = { "ann-acm" };
        assertArrayEquals(acmGroup.getUserMemberIds().toArray(), members);
    }

    @Test
    public void removeMembersFromAdHocGroupTest() throws Exception
    {
        String content = loadFile("data/userMembers.json");

        AcmGroup group = new AcmGroup();
        group.setName("A");
        group.setType(AcmGroupType.ADHOC_GROUP);
        group.setStatus(AcmGroupStatus.ACTIVE);

        AcmUser user1 = new AcmUser();
        user1.setUserId("ann-acm");
        user1.setUserDirectoryName("armedia");
        user1.setUserState(AcmUserState.VALID);

        AcmUser user2 = new AcmUser();
        user2.setUserId("sally-acm");
        user2.setUserDirectoryName("armedia");
        user2.setUserState(AcmUserState.VALID);

        group.addUserMember(user2);

        expect(mockAuthentication.getName()).andReturn("ann-acm");
        expect(groupService.removeMembersFromAdHocGroup(new HashSet<>(Arrays.asList(user1)), "A"))
                .andReturn(group);

        replayAll();

        MvcResult result = mockMvc.perform(
                post("/api/latest/users/group/{groupId}/members/remove/", group.getName())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockAuthentication)
                        .content(content))
                .andReturn();

        verifyAll();

        log.info("Results: {}", result.getResponse().getContentAsString());
        ObjectMapper om = new ObjectMapper();
        AcmGroup acmGroup = om.readValue(result.getResponse().getContentAsString(), AcmGroup.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        String[] members = { "sally-acm" };
        assertArrayEquals(acmGroup.getUserMemberIds().toArray(), members);
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o,
                                         Exception e)
    {
        log.error("An error occurred", e);
        return null;
    }
}
