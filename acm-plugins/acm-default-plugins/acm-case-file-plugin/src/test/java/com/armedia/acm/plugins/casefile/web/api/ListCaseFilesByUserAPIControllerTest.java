package com.armedia.acm.plugins.casefile.web.api;

/*-
 * #%L
 * ACM Default Plugin: Case File
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

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.utility.CaseFileEventUtility;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by marjan.stefanoski on 10/13/2014.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-case-plugin-unit-test.xml"
})

public class ListCaseFilesByUserAPIControllerTest extends EasyMockSupport
{

    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;

    private ListCaseFilesByUserAPIController unit;

    private CaseFileDao mockCaseFileDao;
    private CaseFileEventUtility mockCaseFileEventUtility;
    private Authentication mockAuthentication;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private Logger log = LogManager.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        mockCaseFileDao = createMock(CaseFileDao.class);
        mockCaseFileEventUtility = createMock(CaseFileEventUtility.class);
        mockHttpSession = new MockHttpSession();
        mockAuthentication = createMock(Authentication.class);

        unit = new ListCaseFilesByUserAPIController();

        unit.setCaseFileDao(mockCaseFileDao);
        unit.setCaseFileEventUtility(mockCaseFileEventUtility);

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();
    }

    @Test
    public void caseFilesByUserTest() throws Exception
    {

        String user = "user";

        CaseFile caseFile = new CaseFile();
        caseFile.setId(5L);
        caseFile.setCreator(user);
        caseFile.setCreated(new Date());
        caseFile.setClassName(caseFile.getClass().getName());
        String ipAddress = "ipAddress";

        ObjectMapper om = new ObjectMapper();
        String caseFileJson = om.writeValueAsString(caseFile);
        log.info("caseFileJson: " + caseFileJson);

        expect(mockCaseFileDao.getNotClosedCaseFilesByUser(user)).andReturn(Arrays.asList(caseFile));

        Capture<CaseFile> capturedCase = new Capture<>();

        mockCaseFileEventUtility.raiseEvent(capture(capturedCase), eq("search"), anyObject(Date.class), eq(ipAddress), eq(user),
                eq(mockAuthentication));

        mockHttpSession.setAttribute("acm_ip_address", ipAddress);

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/latest/plugin/casefile/forUser/{user}", user)
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .principal(mockAuthentication)
                        .session(mockHttpSession))
                .andReturn();
        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        String returned = result.getResponse().getContentAsString();

        log.info("results: " + returned);

        ObjectMapper objectMapper = new ObjectMapper();

        List<CaseFile> foundCases = objectMapper.readValue(returned,
                objectMapper.getTypeFactory().constructParametricType(List.class, CaseFile.class));

        assertEquals(1, foundCases.size());

        CaseFile found = foundCases.get(0);
        assertEquals(caseFile.getId(), found.getId());
    }

}
