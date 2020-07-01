package com.armedia.acm.plugins.consultation.web.api;

/*-
 * #%L
 * ACM Default Plugin: Consultation
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.plugins.consultation.service.ConsultationService;
import com.armedia.acm.plugins.consultation.utility.ConsultationEventUtility;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

import java.util.Collections;
import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-consultation-plugin-unit-test.xml"
})

public class GetConsultationAPIControllerTest extends EasyMockSupport
{
    static final String IP_ADDRESS = "127.0.0.1";
    static final String USER_ID = "ann-acm";
    static final Long OBJECT_ID = 1234L;

    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;
    private GetConsultationAPIController unit;
    private ConsultationService mockConsultatonService;
    private ConsultationEventUtility mockConsultationEventUtility;
    private Authentication mockAuthentication;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private Logger log = LogManager.getLogger(getClass());

    @Before
    public void setUp()
    {
        mockConsultatonService = createMock(ConsultationService.class);

        mockConsultationEventUtility = createMock(ConsultationEventUtility.class);
        mockHttpSession = new MockHttpSession();
        mockAuthentication = createMock(Authentication.class);

        unit = new GetConsultationAPIController();

        unit.setConsultationEventUtility(mockConsultationEventUtility);
        unit.setConsultationService(mockConsultatonService);

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();
    }

    private Consultation createTestConsultation()
    {
        Consultation consultation = new Consultation();
        consultation.setId(OBJECT_ID);
        consultation.setCreator(USER_ID);
        consultation.setCreated(new Date());
        consultation.setClassName(consultation.getClass().getName());
        return consultation;
    }

    @Test
    public void listConsultationsForUser() throws Exception
    {
        Consultation consultation = createTestConsultation();

        mockHttpSession.setAttribute("acm_ip_address", IP_ADDRESS);

        log.info("consultation: " + consultation);

        Capture<Consultation> capturedConsultation = EasyMock.newCapture();

        mockConsultationEventUtility.raiseEvent(capture(capturedConsultation), eq("search"), anyObject(Date.class), eq(IP_ADDRESS),
                eq(USER_ID),
                eq(mockAuthentication));

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn(USER_ID).atLeastOnce();
        expect(mockConsultatonService.getNotClosedConsultationsByUser(USER_ID)).andReturn(Collections.singletonList(consultation))
                .anyTimes();

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/latest/plugin/consultation/forUser/{user}", USER_ID)
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

        List<Consultation> foundConsultations = objectMapper.readValue(returned,
                objectMapper.getTypeFactory().constructParametricType(List.class, Consultation.class));

        assertEquals(1, foundConsultations.size());

        Consultation found = foundConsultations.get(0);
        assertEquals(consultation.getId(), found.getId());
    }

    @Test
    public void findConsultationByNumber() throws Exception
    {
        Consultation consultation = createTestConsultation();

        mockHttpSession.setAttribute("acm_ip_address", IP_ADDRESS);

        expect(mockConsultatonService.getConsultationByIdWithChangeStatusIncluded(consultation.getId())).andReturn(consultation);
        mockConsultationEventUtility.raiseConsultationViewed(eq(consultation), eq(mockAuthentication));

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn(USER_ID);

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/v1/plugin/consultation/byId/{consultationId}", consultation.getId())
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .session(mockHttpSession)
                        .principal(mockAuthentication))
                .andReturn();

        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        String json = result.getResponse().getContentAsString();

        log.info("results: " + json);

        Consultation fromJson = new ObjectMapper().readValue(json, Consultation.class);

        assertNotNull(fromJson);
        assertEquals(consultation.getConsultationNumber(), fromJson.getConsultationNumber());
    }

    @Test
    public void findConsultationById() throws Exception
    {
        Consultation consultation = createTestConsultation();

        mockHttpSession.setAttribute("acm_ip_address", IP_ADDRESS);

        expect(mockConsultatonService.getConsultationByIdWithChangeStatusIncluded(consultation.getId())).andReturn(consultation);
        mockConsultationEventUtility.raiseConsultationViewed(eq(consultation), eq(mockAuthentication));

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn(USER_ID);

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/v1/plugin/consultation/byId/{consultationId}", consultation.getId())
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .session(mockHttpSession)
                        .principal(mockAuthentication))
                .andReturn();

        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        String json = result.getResponse().getContentAsString();

        log.info("results: " + json);

        Consultation fromJson = new ObjectMapper().readValue(json, Consultation.class);

        assertNotNull(fromJson);
        assertEquals(consultation.getConsultationNumber(), fromJson.getConsultationNumber());
    }

    @Test
    public void findConsultationById_notFound() throws Exception
    {
        mockHttpSession.setAttribute("acm_ip_address", IP_ADDRESS);

        expect(mockConsultatonService.getConsultationByIdWithChangeStatusIncluded(OBJECT_ID))
                .andThrow(new AcmObjectNotFoundException("Consultation", OBJECT_ID, "test exception"));

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn(USER_ID);

        replayAll();

        mockMvc.perform(
                get("/api/v1/plugin/consultation/byId/{consultationId}", OBJECT_ID)
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .session(mockHttpSession)
                        .principal(mockAuthentication))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN));

        verifyAll();
    }
}
