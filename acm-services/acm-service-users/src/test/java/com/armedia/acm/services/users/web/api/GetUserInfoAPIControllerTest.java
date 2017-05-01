package com.armedia.acm.services.users.web.api;

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserInfoDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.httpclient.HttpStatus;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Created by manoj.dhungana on 5/01/17.
 */
public class GetUserInfoAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;
    private MockHttpSession mockSession;
    private Authentication mockAuthentication;

    private GetUserInfoAPIController unit;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        mockSession = new MockHttpSession();
        mockAuthentication = createMock(Authentication.class);
        unit = new GetUserInfoAPIController();
        mockMvc = MockMvcBuilders.standaloneSetup(unit).build();
    }

    @Test
    public void getUserInfo_test() throws Exception
    {
        String privilege = "privilege";
        Map<String, Boolean> privilegeMap = new HashMap<>();
        privilegeMap.put(privilege, Boolean.TRUE);

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>(Arrays.asList(
                (GrantedAuthority) () -> "INVESTIGATOR",
                (GrantedAuthority) () -> "ADMINISTRATOR"
        ));

        AcmUser acmUser = new AcmUser();
        acmUser.setUserId("user");
        mockSession.setAttribute("acm_user", acmUser);
        mockSession.setAttribute("acm_privileges", privilegeMap);
        expect((List<GrantedAuthority>) mockAuthentication.getAuthorities()).andReturn(grantedAuthorities).atLeastOnce();
        expect(mockAuthentication.getName()).andReturn("USER").anyTimes();

        replayAll();

        MvcResult result =
                mockMvc.perform(get("/api/latest/users/info")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .principal(mockAuthentication).session(mockSession)
                        .contentType(MediaType.APPLICATION_JSON)).andReturn();


        verifyAll();

        String json = result.getResponse().getContentAsString();

        log.info("results: " + result.getResponse().getContentAsString());
        assertEquals(HttpStatus.SC_OK, result.getResponse().getStatus());

        ObjectMapper objectMapper = new ObjectMapper();
        AcmUserInfoDTO retval = objectMapper.readValue(json, AcmUserInfoDTO.class);
        assertNotNull(retval.getUserId());
        assertNotNull(retval.getUserId(), "user");
        assertNotNull(retval.getPrivileges());
        assertEquals(retval.getPrivileges().size(), 1);
        assertNotNull(retval.getAuthorities());
        assertEquals(retval.getAuthorities().size(), 2);
    }
}
