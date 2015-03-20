/**
 * 
 */
package com.armedia.acm.services.costsheet.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.services.costsheet.model.AcmCostsheet;
import com.armedia.acm.services.costsheet.service.CostsheetService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author riste.tutureski
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-library-service-costsheet-test.xml"
})
public class GetCostsheetsForObjectIdAndTypeAPIControllerTest extends EasyMockSupport {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	
	private MockMvc mockMvc;
	private CostsheetService mockCostsheetService;
	private GetCostsheetsForObjectIdAndTypeAPIController unit;
	private Authentication mockAuthentication;
	
	@Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;
	
	@Before
    public void setUp() throws Exception
    {
		mockCostsheetService = createMock(CostsheetService.class);
		unit = new GetCostsheetsForObjectIdAndTypeAPIController();
		mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();
		mockAuthentication = createMock(Authentication.class);
		
		unit.setCostsheetService(mockCostsheetService);
    }
	
	@Test
	public void getCostsheetsByObjectIdAndTypeSuccessTest() throws Exception
	{		
		AcmCostsheet costsheet1 = new AcmCostsheet();
		costsheet1.setId(1L);
		costsheet1.setStatus("status1");
		costsheet1.setDetails("details1");
		costsheet1.setParentId(5L);
		costsheet1.setParentType("TYPE");
		
		AcmCostsheet costsheet2 = new AcmCostsheet();
		costsheet2.setId(2L);
		costsheet2.setStatus("status2");
		costsheet2.setDetails("details2");
		costsheet2.setParentId(6L);
		costsheet2.setParentType("TYPE");
		
		AcmCostsheet costsheet3 = new AcmCostsheet();
		costsheet3.setId(3L);
		costsheet3.setStatus("status3");
		costsheet3.setDetails("details3");
		costsheet3.setParentId(5L);
		costsheet3.setParentType("TYPE");
		
		expect(mockAuthentication.getName()).andReturn("acm-user");
		expect(mockCostsheetService.getByObjectIdAndType(5L, "type", 0, 10, "")).andReturn(Arrays.asList(costsheet1, costsheet3));
		
		replayAll();
		
		MvcResult result = mockMvc.perform(
	            get("/api/v1/service/costsheet/objectId/{objectId}/objectType/{objectType}", 5L, "type")
                    .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .principal(mockAuthentication))
	                .andReturn();
		
		verifyAll();
		
		LOG.info("Results: " + result.getResponse().getContentAsString());
		
		ObjectMapper mapper = new ObjectMapper();
		List<AcmCostsheet> response = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<ArrayList<AcmCostsheet>>(){});
		
		assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
		assertEquals(2, response.size());
	}
	
	@Test
	public void getCostsheetsByObjectIdAndTypeFailedTest() throws Exception
	{		
		Class<?> expectedThrowableClass = AcmListObjectsFailedException.class;
		
		expect(mockAuthentication.getName()).andReturn("acm-user");
		expect(mockCostsheetService.getByObjectIdAndType(5L, "type", 0, 10, "")).andReturn(null);
		
		replayAll();
		
		MvcResult result = null;
		Exception exception = null;
		try
		{
			result = mockMvc.perform(
	            get("/api/v1/service/costsheet/objectId/{objectId}/objectType/{objectType}", 5L, "type")
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
