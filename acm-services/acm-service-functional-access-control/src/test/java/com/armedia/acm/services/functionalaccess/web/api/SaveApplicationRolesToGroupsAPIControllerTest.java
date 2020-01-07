package com.armedia.acm.services.functionalaccess.web.api;

/*-
 * #%L
 * ACM Service: Functional Access Control
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

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.armedia.acm.services.functionalaccess.service.FunctionalAccessService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author riste.tutureski
 */
public class SaveApplicationRolesToGroupsAPIControllerTest extends EasyMockSupport
{

    private Logger LOG = LogManager.getLogger(getClass());

    private MockMvc mockMvc;
    private SaveApplicationRolesToGroupsAPIController unit;
    private Authentication mockAuthentication;

    private FunctionalAccessService mockFunctionalAccessServiceTest;

    private ExceptionHandlerExceptionResolver exceptionResolver;

    @Before
    public void setUp() throws Exception
    {
        setUnit(new SaveApplicationRolesToGroupsAPIController());
        setMockMvc(MockMvcBuilders.standaloneSetup(getUnit()).setHandlerExceptionResolvers(getExceptionResolver()).build());
        setMockAuthentication(createMock(Authentication.class));

        mockFunctionalAccessServiceTest = createMock(FunctionalAccessService.class);

        getUnit().setFunctionalAccessService(mockFunctionalAccessServiceTest);
    }

    @Test
    public void saveApplicationRolesToGroupsTest() throws Exception
    {

        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, String> rolesToGroups = new HashMap<>();
        Map<String, List<String>> input = prepareRoleToGroupsForRetrieving(rolesToGroups);
        String content = objectMapper.writeValueAsString(input);

        Capture<Map<String, List<String>>> found = EasyMock.newCapture();
        Capture<Authentication> auth = EasyMock.newCapture();

        expect(mockFunctionalAccessServiceTest.saveApplicationRolesToGroups(capture(found), capture(auth))).andReturn(true);
        expect(getMockAuthentication().getName()).andReturn("user");

        replayAll();

        MvcResult result = getMockMvc().perform(
                post("/api/v1/functionalaccess/rolestogroups")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(getMockAuthentication())
                        .content(content))
                .andReturn();

        LOG.info("Results: " + result.getResponse().getContentAsString());

        assertEquals("true", result.getResponse().getContentAsString());
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

    }

    private Map<String, List<String>> prepareRoleToGroupsForRetrieving(Map<String, String> rolesToGroups)
    {
        Map<String, List<String>> retval = new HashMap<>();

        if (rolesToGroups != null && rolesToGroups.size() > 0)
        {
            for (Entry<String, String> entry : rolesToGroups.entrySet())
            {
                retval.put(entry.getKey(), Arrays.asList(entry.getValue().split(",")));
            }
        }

        return retval;
    }

    public MockMvc getMockMvc()
    {
        return mockMvc;
    }

    public void setMockMvc(MockMvc mockMvc)
    {
        this.mockMvc = mockMvc;
    }

    public SaveApplicationRolesToGroupsAPIController getUnit()
    {
        return unit;
    }

    public void setUnit(SaveApplicationRolesToGroupsAPIController unit)
    {
        this.unit = unit;
    }

    public Authentication getMockAuthentication()
    {
        return mockAuthentication;
    }

    public void setMockAuthentication(Authentication mockAuthentication)
    {
        this.mockAuthentication = mockAuthentication;
    }

    public ExceptionHandlerExceptionResolver getExceptionResolver()
    {
        return exceptionResolver;
    }

    public void setExceptionResolver(
            ExceptionHandlerExceptionResolver exceptionResolver)
    {
        this.exceptionResolver = exceptionResolver;
    }

}
