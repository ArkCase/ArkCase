/**
 * 
 */
package com.armedia.acm.services.costsheet.web;

/*-
 * #%L
 * ACM Service: Costsheet
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
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.services.costsheet.model.AcmCost;
import com.armedia.acm.services.costsheet.model.AcmCostsheet;
import com.armedia.acm.services.costsheet.service.CostsheetService;
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
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.util.Arrays;

/**
 * @author riste.tutureski
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-library-service-costsheet-test.xml"
})
public class SaveCostheetAPIControllerTest extends EasyMockSupport
{

    private Logger LOG = LogManager.getLogger(getClass());

    private MockMvc mockMvc;
    private CostsheetService mockCostsheetService;
    private SaveCostsheetAPIController unit;
    private Authentication mockAuthentication;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    @Before
    public void setUp() throws Exception
    {
        mockCostsheetService = createMock(CostsheetService.class);
        unit = new SaveCostsheetAPIController();
        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();
        mockAuthentication = createMock(Authentication.class);

        unit.setCostsheetService(mockCostsheetService);
    }

    @Test
    public void saveCostsheetDetailsSuccessTest() throws Exception
    {
        String expectedDetails = "Details";

        AcmCostsheet costsheet = new AcmCostsheet();
        costsheet.setId(1L);
        costsheet.setStatus("status");
        costsheet.setParentId(5L);
        costsheet.setParentType("TYPE");

        AcmCost cost1 = new AcmCost();
        cost1.setId(3L);
        cost1.setCostsheet(costsheet);
        cost1.setTitle("title");
        cost1.setValue(8.0);

        AcmCost cost2 = new AcmCost();
        cost2.setId(4L);
        cost2.setCostsheet(costsheet);
        cost2.setTitle("title");
        cost2.setValue(7.0);

        costsheet.setCosts(Arrays.asList(cost1, cost2));

        Capture<AcmCostsheet> saved = EasyMock.newCapture();

        expect(mockAuthentication.getName()).andReturn("acm-user");
        Capture<Authentication> capturedAuthentication = EasyMock.newCapture();
        expect(mockCostsheetService.save(capture(saved), capture(capturedAuthentication), eq("Save"))).andReturn(costsheet);

        costsheet.setDetails(expectedDetails);

        ObjectMapper mapper = new ObjectMapper();
        String content = mapper.writeValueAsString(costsheet);

        replayAll();

        MvcResult result = mockMvc.perform(
                post("/api/v1/service/costsheet/{submissionName}", "Save")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockAuthentication)
                        .content(content))
                .andReturn();

        verifyAll();

        LOG.info("Results: " + result.getResponse().getContentAsString());

        AcmCostsheet response = mapper.readValue(result.getResponse().getContentAsString(), AcmCostsheet.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals(expectedDetails, response.getDetails());
    }

    @Test
    public void saveCostsheetDetailsFailedTest() throws Exception
    {
        AcmCostsheet costsheet = new AcmCostsheet();
        costsheet.setId(1L);
        costsheet.setStatus("status");
        costsheet.setParentId(5L);
        costsheet.setParentType("TYPE");

        AcmCost cost1 = new AcmCost();
        cost1.setId(3L);
        cost1.setCostsheet(costsheet);
        cost1.setTitle("title");
        cost1.setValue(8.0);

        AcmCost cost2 = new AcmCost();
        cost2.setId(4L);
        cost2.setCostsheet(costsheet);
        cost2.setTitle("title");
        cost2.setValue(7.0);

        costsheet.setCosts(Arrays.asList(cost1, cost2));

        ObjectMapper mapper = new ObjectMapper();
        String content = mapper.writeValueAsString(costsheet);

        Class<?> expectedThrowableClass = AcmCreateObjectFailedException.class;
        Capture<AcmCostsheet> saved = EasyMock.newCapture();

        expect(mockAuthentication.getName()).andReturn("acm-user");
        Capture<Authentication> capturedAuthentication = EasyMock.newCapture();
        expect(mockCostsheetService.save(capture(saved), capture(capturedAuthentication), eq("Save"))).andThrow(new RuntimeException());

        replayAll();

        MvcResult result = null;
        Exception exception = null;
        try
        {
            result = mockMvc.perform(
                    post("/api/v1/service/costsheet/{submissionName}", "Save")
                            .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .principal(mockAuthentication)
                            .content(content))
                    .andReturn();
        }
        catch (Exception e)
        {
            exception = e;
        }

        verifyAll();

        assertEquals(null, result);
        assertEquals(expectedThrowableClass, exception.getCause().getClass());
    }

}
