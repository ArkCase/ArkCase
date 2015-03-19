/**
 * 
 */
package com.armedi.acm.services.timesheet.web;

import org.apache.commons.io.IOUtils;
import org.easymock.EasyMockSupport;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.services.search.service.SearchResults;
import com.armedia.acm.services.timesheet.service.TimesheetService;
import com.armedia.acm.services.timesheet.web.GetTimesheetsForUserAPIController;

/**
 * @author riste.tutureski
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-library-service-timesheet-test.xml"
})
public class GetTimesheetsForUserAPIControllerTest extends EasyMockSupport {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	
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
		expect(mockTimesheetService.getObjectsFromSolr("TIMESHEET", mockAuthentication, 0, 10, "", "acm-user")).andReturn(expected);
		
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
		expect(mockTimesheetService.getObjectsFromSolr("TIMESHEET", mockAuthentication, 0, 10, "", "acm-user")).andReturn(null);
		
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
		catch(Exception e)
		{
			exception = e;
		}
		
		verifyAll();
		
		assertEquals(null, result);
		assertEquals(expectedThrowableClass, exception.getCause().getClass());
	}	
}
