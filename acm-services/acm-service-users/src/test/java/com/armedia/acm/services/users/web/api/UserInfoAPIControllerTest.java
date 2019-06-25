package com.armedia.acm.services.users.web.api;

/*-
 * #%L
 * ACM Service: Users
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserInfoDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.httpclient.HttpStatus;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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

/**
 * Created by manoj.dhungana on 5/01/17.
 */
public class UserInfoAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;
    private MockHttpSession mockSession;
    private Authentication mockAuthentication;

    private UserInfoAPIController unit;

    private Logger log = LogManager.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        mockSession = new MockHttpSession();
        mockAuthentication = createMock(Authentication.class);
        unit = new UserInfoAPIController();
        mockMvc = MockMvcBuilders.standaloneSetup(unit).build();
    }

    @Test
    public void getUserInfo_test() throws Exception
    {
        String privilege = "privilege";
        Map<String, Boolean> privilegeMap = new HashMap<>();
        privilegeMap.put(privilege, Boolean.TRUE);

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>(
                Arrays.asList((GrantedAuthority) () -> "INVESTIGATOR", (GrantedAuthority) () -> "ADMINISTRATOR"));

        AcmUser acmUser = new AcmUser();
        acmUser.setUserId("user");
        acmUser.setLang("en");
        mockSession.setAttribute("acm_user", acmUser);
        mockSession.setAttribute("acm_privileges", privilegeMap);
        expect((List<GrantedAuthority>) mockAuthentication.getAuthorities()).andReturn(grantedAuthorities).atLeastOnce();
        expect(mockAuthentication.getName()).andReturn("USER").anyTimes();

        replayAll();

        MvcResult result = mockMvc.perform(get("/api/latest/users/info").accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                .principal(mockAuthentication).session(mockSession).contentType(MediaType.APPLICATION_JSON)).andReturn();

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
        assertEquals(retval.getLangCode(), "en");
    }
}
