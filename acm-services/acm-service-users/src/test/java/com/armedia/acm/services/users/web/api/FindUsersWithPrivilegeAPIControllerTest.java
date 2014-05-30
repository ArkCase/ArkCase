package com.armedia.acm.services.users.web.api;

import com.armedia.acm.pluginmanager.service.AcmPluginManager;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * Created by armdev on 5/28/14.
 */
public class FindUsersWithPrivilegeAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;
    private AcmPluginManager mockPluginManager;
    private UserDao mockUserDao;

    private FindUsersWithPrivilegeAPIController unit;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        mockPluginManager = createMock(AcmPluginManager.class);
        mockUserDao = createMock(UserDao.class);

        unit = new FindUsersWithPrivilegeAPIController();
        unit.setPluginManager(mockPluginManager);
        unit.setUserDao(mockUserDao);

        mockMvc = MockMvcBuilders.standaloneSetup(unit).build();
    }

    @Test
    public void withPrivilege() throws Exception
    {
        List<String> roles = Arrays.asList("Role 1", "Role 2");
        String privilege = "acm-complaint-approve";

        AcmUser user1 = new AcmUser();
        AcmUser user2 = new AcmUser();
        List<AcmUser> users = Arrays.asList(user1, user2);

        expect(mockPluginManager.getRolesForPrivilege(privilege)).andReturn(roles);
        expect(mockUserDao.findUsersWithRoles(roles)).andReturn(users);

        replayAll();

        MvcResult result =
                mockMvc.perform(get("/api/latest/users/withPrivilege/{privilege}", privilege)
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .contentType(MediaType.APPLICATION_JSON)).andReturn();

        log.info("results: " + result.getResponse().getContentAsString());

        verifyAll();
    }
}
