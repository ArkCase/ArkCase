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

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.armedia.acm.form.config.FormsTypeCheckService;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.complaint.ComplaintForm;
import com.armedia.acm.plugins.complaint.service.ComplaintEventPublisher;
import com.armedia.acm.plugins.complaint.service.ComplaintService;
import com.armedia.acm.plugins.complaint.service.SaveComplaintTransaction;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAssociation;
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
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.util.Arrays;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-complaint-plugin-unit-test.xml"
})
public class CreateComplaintAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;

    private CreateComplaintAPIController unit;
    private SaveComplaintTransaction mockSaveTransaction;
    private ComplaintEventPublisher mockEventPublisher;
    private Authentication mockAuthentication;
    private ComplaintService mockComplaintService;
    private FormsTypeCheckService mockFormsTypeCheckService;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private Logger log = LogManager.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        unit = new CreateComplaintAPIController();
        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();

        mockSaveTransaction = createMock(SaveComplaintTransaction.class);
        mockEventPublisher = createMock(ComplaintEventPublisher.class);
        mockAuthentication = createMock(Authentication.class);
        mockComplaintService = createMock(ComplaintService.class);
        mockFormsTypeCheckService = createMock(FormsTypeCheckService.class);

        unit.setEventPublisher(mockEventPublisher);
        unit.setComplaintService(mockComplaintService);
        unit.setObjectConverter(ObjectConverter.createObjectConverterForTests());
        unit.setFormsTypeCheckService(mockFormsTypeCheckService);
    }

    @Test
    public void createComplaint_saveExistingComplaint() throws Exception
    {
        Complaint complaint = new Complaint();
        complaint.setComplaintId(500L);
        complaint.setComplaintType("complaintType");

        Person person = new Person();
        person.setFamilyName("Jones");
        person.setGivenName("David");
        PostalAddress address = new PostalAddress();
        address.setCity("Falls Church");
        address.setState("VA");
        address.setStreetAddress("8221 Old Courthouse Road");
        person.getAddresses().add(address);

        PersonAssociation personAssoc = new PersonAssociation();
        personAssoc.setPerson(person);
        personAssoc.setPersonDescription("sample Description");
        personAssoc.setPersonType("Originator");

        complaint.setOriginator(personAssoc);

        complaint.setApprovers(Arrays.asList("user1", "user2"));

        Complaint saved = new Complaint();
        saved.setComplaintId(complaint.getComplaintId());
        saved.setComplaintNumber("testNumber");

        ObjectMapper objectMapper = new ObjectMapper();
        String in = objectMapper.writeValueAsString(complaint);

        log.debug("Input JSON: " + in);

        Capture<Complaint> found = Capture.newInstance();
        Capture<Complaint> foundOldComplaint = Capture.newInstance();

        Complaint oldComplaint = new Complaint();
        oldComplaint.setComplaintTitle("the old complaint title");
        expect(mockComplaintService.getSaveComplaintTransaction()).andReturn(mockSaveTransaction);
        expect(mockSaveTransaction.getComplaint(complaint.getComplaintId())).andReturn(oldComplaint);

        mockComplaintService.updateXML(capture(found), eq(mockAuthentication), eq(ComplaintForm.class));
        expectLastCall().anyTimes();
        expect(mockComplaintService.saveComplaint(capture(found), eq(mockAuthentication))).andReturn(saved);
        mockEventPublisher.publishComplaintEvent(capture(found), capture(foundOldComplaint), eq(mockAuthentication), eq(false), eq(true));

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        expect(mockFormsTypeCheckService.getTypeOfForm()).andReturn("frevvo");

        replayAll();

        MvcResult result = mockMvc.perform(
                post("/api/latest/plugin/complaint")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockAuthentication)
                        .content(in))
                .andReturn();

        log.info("results: " + result.getResponse().getContentAsString());

        verifyAll();

        assertEquals(saved, found.getValue());

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

        String returned = result.getResponse().getContentAsString();

        Complaint mapped = objectMapper.readValue(returned, Complaint.class);
        assertEquals(saved.getComplaintNumber(), mapped.getComplaintNumber());

        assertEquals(complaint.getApprovers(), saved.getApprovers());

        assertNotNull(foundOldComplaint.getValue());
        assertEquals(oldComplaint.getComplaintTitle(), foundOldComplaint.getValue().getComplaintTitle());

    }

    @Test
    public void invalidInput() throws Exception
    {
        String notComplaintJson = "{ \"user\": \"dmiller\",\"className\":\"com.armedia.acm.plugins.complaint.model.Complaint\" }";

        Capture<Complaint> found = EasyMock.newCapture();

        mockComplaintService.updateXML(capture(found), eq(mockAuthentication), eq(ComplaintForm.class));
        expectLastCall().anyTimes();
        expect(mockComplaintService.saveComplaint(capture(found), eq(mockAuthentication))).andThrow(new RuntimeException());

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        replayAll();

        mockMvc.perform(
                post("/api/latest/plugin/complaint")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockAuthentication)
                        .content(notComplaintJson))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN));

        verifyAll();

    }

    @Test
    public void createComplaint_exception() throws Exception
    {
        Complaint complaint = new Complaint();
        complaint.setComplaintId(500L);
        complaint.setComplaintType("complaintType");

        Person person = new Person();
        person.setFamilyName("Jones");
        person.setGivenName("David");
        PostalAddress address = new PostalAddress();
        address.setCity("Falls Church");
        address.setState("VA");
        address.setStreetAddress("8221 Old Courthouse Road");
        person.getAddresses().add(address);

        PersonAssociation personAssoc = new PersonAssociation();
        personAssoc.setPerson(person);
        personAssoc.setPersonDescription("sample Description");
        personAssoc.setPersonType("Originator");

        complaint.setOriginator(personAssoc);

        complaint.setApprovers(Arrays.asList("user1", "user2"));

        ObjectMapper objectMapper = new ObjectMapper();
        String in = objectMapper.writeValueAsString(complaint);

        log.debug("Input JSON: " + in);

        Capture<Complaint> found = Capture.newInstance();
        Capture<Complaint> foundOldComplaint = Capture.newInstance();

        Complaint oldComplaint = new Complaint();
        oldComplaint.setComplaintTitle("the old complaint title");
        expect(mockComplaintService.getSaveComplaintTransaction()).andReturn(mockSaveTransaction);
        expect(mockSaveTransaction.getComplaint(complaint.getComplaintId())).andReturn(oldComplaint);
        expect(mockComplaintService.saveComplaint(capture(found), eq(mockAuthentication)))
                .andThrow(new CannotCreateTransactionException("testException"));
        mockEventPublisher.publishComplaintEvent(capture(found), capture(foundOldComplaint), eq(mockAuthentication), eq(false), eq(false));

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        replayAll();

        mockMvc.perform(
                post("/api/latest/plugin/complaint")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockAuthentication)
                        .content(in))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN));

        verifyAll();

        assertEquals(complaint.getComplaintId(), found.getValue().getComplaintId());

        // when an error is thrown, the event will not have the old complaint value, so we should get a null... not the
        // old complaint.
        assertNull(foundOldComplaint.getValue());

    }

}
