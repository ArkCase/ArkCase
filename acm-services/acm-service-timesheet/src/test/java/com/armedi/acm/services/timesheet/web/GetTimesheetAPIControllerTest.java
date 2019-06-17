/**
 * 
 */
package com.armedi.acm.services.timesheet.web;

/*-
 * #%L
 * ACM Service: Timesheet
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

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.services.timesheet.model.AcmTime;
import com.armedia.acm.services.timesheet.model.AcmTimesheet;
import com.armedia.acm.services.timesheet.service.TimesheetService;
import com.armedia.acm.services.timesheet.web.GetTimesheetAPIController;
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
        "classpath:/spring/spring-library-service-timesheet-test.xml"
})
public class GetTimesheetAPIControllerTest extends EasyMockSupport
{

    private Logger LOG = LogManager.getLogger(getClass());

    private MockMvc mockMvc;
    private TimesheetService mockTimesheetService;
    private GetTimesheetAPIController unit;
    private Authentication mockAuthentication;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    @Before
    public void setUp() throws Exception
    {
        mockTimesheetService = createMock(TimesheetService.class);
        unit = new GetTimesheetAPIController();
        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();
        mockAuthentication = createMock(Authentication.class);

        unit.setTimesheetService(mockTimesheetService);
    }

    @Test
    public void getTimesheetSuccessTest() throws Exception
    {
        AcmTimesheet timesheet = new AcmTimesheet();
        timesheet.setId(1L);
        timesheet.setStatus("status");
        timesheet.setDetails("details");

        AcmTime time1 = new AcmTime();
        time1.setId(3L);
        time1.setTimesheet(timesheet);
        time1.setCode("code1");
        time1.setType("type1");
        time1.setValue(8.0);

        AcmTime time2 = new AcmTime();
        time2.setId(4L);
        time2.setTimesheet(timesheet);
        time2.setCode("code2");
        time2.setType("type2");
        time2.setValue(7.0);

        timesheet.setTimes(Arrays.asList(time1, time2));

        expect(mockAuthentication.getName()).andReturn("acm-user");
        expect(mockTimesheetService.get(1L)).andReturn(timesheet);

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/v1/service/timesheet/{id}", 1L)
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockAuthentication))
                .andReturn();

        verifyAll();

        LOG.info("Results: " + result.getResponse().getContentAsString());

        ObjectMapper mapper = new ObjectMapper();
        AcmTimesheet response = mapper.readValue(result.getResponse().getContentAsString(), AcmTimesheet.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals(timesheet.getId(), response.getId());
    }

    @Test
    public void getTimesheetFailedTest() throws Exception
    {
        Class<?> expectedThrowableClass = AcmObjectNotFoundException.class;

        expect(mockAuthentication.getName()).andReturn("acm-user");
        expect(mockTimesheetService.get(1L)).andReturn(null);

        replayAll();

        MvcResult result = null;
        Exception exception = null;
        try
        {
            result = mockMvc.perform(
                    get("/api/v1/service/timesheet/{id}", 1L)
                            .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .principal(mockAuthentication))
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
