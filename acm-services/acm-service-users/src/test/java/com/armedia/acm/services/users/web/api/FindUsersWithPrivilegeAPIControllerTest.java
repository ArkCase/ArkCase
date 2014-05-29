package com.armedia.acm.services.users.web.api;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * Created by armdev on 5/28/14.
 */
public class FindUsersWithPrivilegeAPIControllerTest
{
    private MockMvc mockMvc;

    private FindUsersWithPrivilegeAPIController unit;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        unit = new FindUsersWithPrivilegeAPIController();
        mockMvc = MockMvcBuilders.standaloneSetup(unit).build();
    }

    @Test
    public void withPrivilege() throws Exception
    {
        MvcResult result =
                mockMvc.perform(get("/api/latest/users/withPrivilege/{privilege}", "acm-complaint-approve")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .contentType(MediaType.APPLICATION_JSON)).andReturn();

        log.info("results: " + result.getResponse().getContentAsString());
    }
}
