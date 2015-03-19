/**
 * 
 */
package com.armedi.acm.services.timesheet.web;

import java.util.Arrays;

import org.easymock.EasyMockSupport;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

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

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.services.timesheet.model.AcmTime;
import com.armedia.acm.services.timesheet.model.AcmTimesheet;
import com.armedia.acm.services.timesheet.service.TimesheetService;
import com.armedia.acm.services.timesheet.web.GetTimesheetAPIController;
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
		catch(Exception e)
		{
			exception = e;
		}
		
		verifyAll();
		
		assertEquals(null, result);
		assertEquals(expectedThrowableClass, exception.getCause().getClass());
	}	
}
