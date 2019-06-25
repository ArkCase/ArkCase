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
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.armedia.acm.services.config.lookups.model.StandardLookup;
import com.armedia.acm.services.config.lookups.model.StandardLookupEntry;
import com.armedia.acm.services.config.lookups.service.LookupDao;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.castor.CastorMarshaller;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-complaint-plugin-unit-test.xml" })
public class GetComplaintListOfValuesAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;
    private Authentication mockAuthentication;
    private LookupDao mockLookupDao;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private GetComplaintListOfValuesAPIController unit;

    private Logger log = LogManager.getLogger(getClass());

    private StandardLookup complaintPrioritiesLookup;

    private StandardLookup complaintTypesLookup;

    @Before
    public void setUp() throws Exception
    {
        unit = new GetComplaintListOfValuesAPIController();

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();

        mockAuthentication = createMock(Authentication.class);

        complaintPrioritiesLookup = new StandardLookup();
        List<StandardLookupEntry> complaintPrioritiesLookupEntries = new ArrayList<>();
        complaintPrioritiesLookupEntries.add(new StandardLookupEntry("1", "value1"));
        complaintPrioritiesLookupEntries.add(new StandardLookupEntry("2", "value2"));
        complaintPrioritiesLookupEntries.add(new StandardLookupEntry("3", "value3"));
        complaintPrioritiesLookupEntries.add(new StandardLookupEntry("4", "value4"));
        complaintPrioritiesLookup.setEntries(complaintPrioritiesLookupEntries);

        complaintTypesLookup = new StandardLookup();
        List<StandardLookupEntry> complaintTypesLookupEntries = new ArrayList<>();
        complaintTypesLookupEntries.add(new StandardLookupEntry("A", "valueA"));
        complaintTypesLookupEntries.add(new StandardLookupEntry("B", "valueB"));
        complaintTypesLookupEntries.add(new StandardLookupEntry("C", "valueC"));
        complaintTypesLookupEntries.add(new StandardLookupEntry("D", "valueD"));
        complaintTypesLookup.setEntries(complaintTypesLookupEntries);

        mockLookupDao = createMock(LookupDao.class);

        unit.setLookupDao(mockLookupDao);
    }

    @Test
    public void getComplaintTypes() throws Exception
    {
        String[] typeList = {
                "A",
                "B",
                "C",
                "D" };

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");
        expect((StandardLookup) mockLookupDao.getLookupByName("complaintTypes")).andReturn(complaintTypesLookup);
        replayAll();

        MvcResult result = mockMvc.perform(get("/api/latest/plugin/complaint/types")
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")).principal(mockAuthentication)).andReturn();

        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        String returned = result.getResponse().getContentAsString();

        log.info("results: " + returned);

        ObjectMapper objectMapper = new ObjectMapper();

        String types[] = objectMapper.readValue(returned, String[].class);

        assertEquals(4, types.length);

        assertArrayEquals(typeList, types);

    }

    @Test
    public void getComplaintPriorities() throws Exception
    {
        String[] priorityList = {
                "1",
                "2",
                "3",
                "4" };

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");
        expect((StandardLookup) mockLookupDao.getLookupByName("priorities")).andReturn(complaintPrioritiesLookup);

        replayAll();

        MvcResult result = mockMvc.perform(get("/api/latest/plugin/complaint/priorities")
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")).principal(mockAuthentication)).andReturn();

        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        String returned = result.getResponse().getContentAsString();

        log.info("results: " + returned);

        ObjectMapper objectMapper = new ObjectMapper();

        String priorities[] = objectMapper.readValue(returned, String[].class);

        assertEquals(4, priorities.length);

        assertArrayEquals(priorityList, priorities);

    }

    @Test
    public void getComplaintPriorities_xml() throws Exception
    {

        MarshallingHttpMessageConverter xmlConverter = new MarshallingHttpMessageConverter();
        CastorMarshaller marshaller = new CastorMarshaller();
        marshaller.afterPropertiesSet();
        marshaller.setValidating(false);
        xmlConverter.setMarshaller(marshaller);
        xmlConverter.setUnmarshaller(marshaller);

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).setMessageConverters(xmlConverter)
                .build();

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");
        expect((StandardLookup) mockLookupDao.getLookupByName("priorities")).andReturn(complaintPrioritiesLookup);

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/latest/plugin/complaint/priorities").accept(MediaType.parseMediaType("text/xml")).principal(mockAuthentication))
                .andReturn();

        verifyAll();

        String returned = result.getResponse().getContentAsString();
        log.info("results: " + returned);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.TEXT_XML_VALUE));

    }

}
