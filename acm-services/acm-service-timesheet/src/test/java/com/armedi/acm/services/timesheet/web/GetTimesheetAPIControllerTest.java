/**
 * 
 */
package com.armedi.acm.services.timesheet.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.services.search.service.SearchResults;
import com.armedia.acm.services.timesheet.model.AcmTime;
import com.armedia.acm.services.timesheet.model.AcmTimesheet;
import com.armedia.acm.services.timesheet.service.TimesheetService;
import com.armedia.acm.services.timesheet.web.GetTimesheetAPIController;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author riste.tutureski
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-library-service-timesheet-test.xml"
})
public class GetTimesheetAPIControllerTest extends EasyMockSupport {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	
	private MockMvc mockMvc;
	private TimesheetService mockTimesheetService;
	private GetTimesheetAPIController unit;
	private Authentication mockAuthentication;
	private SearchResults searchResults;
	
	@Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;
	
	@Before
    public void setUp() throws Exception
    {
		mockTimesheetService = createMock(TimesheetService.class);
		unit = new GetTimesheetAPIController();
		mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();
		mockAuthentication = createMock(Authentication.class);
		searchResults = new SearchResults();
		
		unit.setTimesheetService(mockTimesheetService);
    }
	
	@Test
	public void getAllTimesheetsSuccessTest() throws Exception
	{
		String expected = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("txt/expectedSolrResponse.txt"));
		
		expect(mockAuthentication.getName()).andReturn("acm-user");
		expect(mockTimesheetService.getObjectsFromSolr("TIMESHEET", mockAuthentication, 0, 10, "")).andReturn(expected);
		
		replayAll();
		
		MvcResult result = mockMvc.perform(
	            get("/api/v1/timesheet/all")
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
	public void getAllTimesheetsFailedTest() throws Exception
	{		
		Class<?> expectedThrowableClass = AcmListObjectsFailedException.class;
		
		expect(mockAuthentication.getName()).andReturn("acm-user");
		expect(mockTimesheetService.getObjectsFromSolr("TIMESHEET", mockAuthentication, 0, 10, "")).andReturn(null);
		
		replayAll();
		
		MvcResult result = null;
		Exception exception = null;
		try
		{
			result = mockMvc.perform(
	            get("/api/v1/timesheet/all")
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
	
	@Test
	public void getTimesheetsSuccessTest() throws Exception
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
	            get("/api/v1/timesheet/{id}", 1L)
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
	public void getTimesheetsFailedTest() throws Exception
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
	            get("/api/v1/timesheet/{id}", 1L)
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
	
	@Test
	public void getTimesheetsByObjectIdSuccessTest() throws Exception
	{		
		AcmTimesheet timesheet = new AcmTimesheet();
		timesheet.setId(1L);
		timesheet.setStatus("status");
		timesheet.setDetails("details");
		
		AcmTime time1 = new AcmTime();
		time1.setId(3L);
		time1.setObjectId(5L);
		time1.setTimesheet(timesheet);
		time1.setCode("code1");
		time1.setType("type1");
		time1.setValue(8.0);
		
		AcmTime time2 = new AcmTime();
		time2.setId(4L);
		time1.setObjectId(5L);
		time2.setTimesheet(timesheet);
		time2.setCode("code2");
		time2.setType("type2");
		time2.setValue(7.0);
		
		timesheet.setTimes(Arrays.asList(time1, time2));
		
		expect(mockAuthentication.getName()).andReturn("acm-user");
		expect(mockTimesheetService.getByObjectId(5L)).andReturn(Arrays.asList(timesheet));
		
		replayAll();
		
		MvcResult result = mockMvc.perform(
	            get("/api/v1/timesheet/objectId/{objectId}", 5L)
                    .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .principal(mockAuthentication))
	                .andReturn();
		
		verifyAll();
		
		LOG.info("Results: " + result.getResponse().getContentAsString());
		
		ObjectMapper mapper = new ObjectMapper();
		List<AcmTimesheet> response = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<ArrayList<AcmTimesheet>>(){});
		
		assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
		assertEquals(1, response.size());
		assertEquals(timesheet.getId(), response.get(0).getId());
	}
	
	@Test
	public void getTimesheetsByObjectIdFailedTest() throws Exception
	{		
		Class<?> expectedThrowableClass = AcmListObjectsFailedException.class;
		
		expect(mockAuthentication.getName()).andReturn("acm-user");
		expect(mockTimesheetService.getByObjectId(5L)).andReturn(null);
		
		replayAll();
		
		MvcResult result = null;
		Exception exception = null;
		try
		{
			result = mockMvc.perform(
	            get("/api/v1/timesheet/objectId/{objectId}", 5L)
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
