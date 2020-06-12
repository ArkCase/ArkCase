package com.armedia.acm.plugins.complaint.web.api;

/*-
 * #%L
 * ACM Default Plugin: Complaints
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.ComplaintListView;
import com.armedia.acm.plugins.complaint.service.ComplaintEventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

/**
 * Created by marjan.stefanoski on 8/20/2014.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-complaint-plugin-unit-test.xml"
})
public class FindComplaintsByUserAPIControllerTest extends EasyMockSupport
{

    private MockMvc mockMvc;
    private Authentication mockAuthentication;
    private ComplaintDao mockComplaintDao;
    private ComplaintEventPublisher mockEventPublisher;
    private MockHttpSession mockHttpSession;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private FindComplaintsByUserAPIController unit;
    private Logger log = LogManager.getLogger(getClass());

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

        String userId = "user";
        ComplaintListView complaint = new ComplaintListView();

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        expect(mockComplaintDao.listAllUserComplaints(userId)).andReturn(Arrays.asList(complaint));

        mockHttpSession.setAttribute("acm_ip_address", "acm_ip_address");

        mockEventPublisher.publishComplaintSearchResultEvent(complaint, mockAuthentication, "acm_ip_address");

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/latest/plugin/complaint/forUser/{user}", userId)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .principal(mockAuthentication)
                        .session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
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
