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

import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.services.search.service.SearchResults;
import com.armedia.acm.services.timesheet.service.TimesheetService;
import com.armedia.acm.services.timesheet.web.GetTimesheetsForUserAPIController;

import org.apache.commons.io.IOUtils;
import org.easymock.EasyMockSupport;
import org.json.JSONArray;
import org.json.JSONObject;
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

/**
 * @author riste.tutureski
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-library-service-timesheet-test.xml"
})
public class GetTimesheetsForUserAPIControllerTest extends EasyMockSupport
{

    private Logger LOG = LogManager.getLogger(getClass());

    private MockMvc mockMvc;
    private TimesheetService mockTimesheetService;
    private GetTimesheetsForUserAPIController unit;
    private Authentication mockAuthentication;
    private SearchResults searchResults;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    @Before
    public void setUp() throws Exception
    {
        mockTimesheetService = createMock(TimesheetService.class);
        unit = new GetTimesheetsForUserAPIController();
        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();
        mockAuthentication = createMock(Authentication.class);
        searchResults = new SearchResults();

        unit.setTimesheetService(mockTimesheetService);
    }

    @Test
    public void getTimesheetsForUserSuccessTest() throws Exception
    {
        String expected = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("txt/expectedSolrResponse.txt"));

        expect(mockAuthentication.getName()).andReturn("acm-user");
        expect(mockTimesheetService.getObjectsFromSolr("TIMESHEET", mockAuthentication, 0, 10, "", "*", "acm-user")).andReturn(expected);

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/v1/service/timesheet/user/{userId}", "acm-user")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockAuthentication))
                .andReturn();

        verifyAll();

        LOG.info("Results: " + result.getResponse().getContentAsString());

        int numFound = searchResults.getNumFound(result.getResponse().getContentAsString());
        JSONArray docs = searchResults.getDocuments(result.getResponse().getContentAsString());
        JSONObject doc = docs.getJSONObject(0);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals(1, numFound);
        assertEquals("0001-TIMESHEET", doc.getString("id"));
    }

    @Test
    public void getTimesheetsForUserFailedTest() throws Exception
    {
        Class<?> expectedThrowableClass = AcmListObjectsFailedException.class;

        expect(mockAuthentication.getName()).andReturn("acm-user");
        expect(mockTimesheetService.getObjectsFromSolr("TIMESHEET", mockAuthentication, 0, 10, "", "*", "acm-user")).andReturn(null);

        replayAll();

        MvcResult result = null;
        Exception exception = null;
        try
        {
            result = mockMvc.perform(
                    get("/api/v1/service/timesheet/user/{userId}", "acm-user")
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
