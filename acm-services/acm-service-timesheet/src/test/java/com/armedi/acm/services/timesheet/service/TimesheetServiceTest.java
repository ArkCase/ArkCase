/**
 * 
 */
package com.armedi.acm.services.timesheet.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.easymock.Capture;
import org.easymock.EasyMockSupport;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.capture;
import static org.junit.Assert.assertEquals;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchResults;
import com.armedia.acm.services.timesheet.dao.AcmTimesheetDao;
import com.armedia.acm.services.timesheet.model.AcmTime;
import com.armedia.acm.services.timesheet.model.AcmTimesheet;
import com.armedia.acm.services.timesheet.service.TimesheetServiceImpl;

/**
 * @author riste.tutureski
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-library-service-timesheet-test.xml"
})
public class TimesheetServiceTest extends EasyMockSupport {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	
	private TimesheetServiceImpl timesheetService;
	private SearchResults searchResults;
	private Authentication mockAuthentication;
	private AcmTimesheetDao mockAcmTimesheetDao;
	private ExecuteSolrQuery mockExecuteSolrQuery;
	private Map<String, String> submissionStatusesMap;
	
	@Before
    public void setUp() throws Exception
    {
		timesheetService = new TimesheetServiceImpl();
		searchResults = new SearchResults();
		mockAuthentication = createMock(Authentication.class);
		mockAcmTimesheetDao = createMock(AcmTimesheetDao.class);
		mockExecuteSolrQuery = createMock(ExecuteSolrQuery.class);
		
		submissionStatusesMap = new HashMap<String, String>();
		submissionStatusesMap.put("Save", "DRAFT");
		submissionStatusesMap.put("Submit", "IN_APPROVAL");
		
		timesheetService.setAcmTimesheetDao(mockAcmTimesheetDao);
		timesheetService.setExecuteSolrQuery(mockExecuteSolrQuery);
		timesheetService.setSubmissionStatusesMap(submissionStatusesMap);		
    }
	
	
	@Test
	public void saveTimesheetTest() throws Exception
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
		
		Capture<AcmTimesheet> timesheetCapture = new Capture<AcmTimesheet>();
		
		expect(mockAcmTimesheetDao.save(capture(timesheetCapture))).andReturn(timesheet);
		
		replayAll();
		
		AcmTimesheet saved = timesheetService.save(timesheet);
		
		verifyAll();

		assertEquals(saved.getId(), timesheetCapture.getValue().getId());
	}
	
	@Test
	public void saveTimesheetAsDraftTest() throws Exception
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
		
		Capture<AcmTimesheet> timesheetCapture = new Capture<AcmTimesheet>();
		
		expect(mockAcmTimesheetDao.save(capture(timesheetCapture))).andReturn(timesheet);
		
		replayAll();
		
		AcmTimesheet saved = timesheetService.save(timesheet, "Save");
		
		verifyAll();

		assertEquals(timesheetCapture.getValue().getId(), saved.getId());
		assertEquals("DRAFT", saved.getStatus());
	}
	
	@Test
	public void saveTimesheetAsInApprovalTest() throws Exception
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
		
		Capture<AcmTimesheet> timesheetCapture = new Capture<AcmTimesheet>();
		
		expect(mockAcmTimesheetDao.save(capture(timesheetCapture))).andReturn(timesheet);
		
		replayAll();
		
		AcmTimesheet saved = timesheetService.save(timesheet, "Submit");
		
		verifyAll();

		assertEquals(timesheetCapture.getValue().getId(), saved.getId());
		assertEquals("IN_APPROVAL", saved.getStatus());
	}
	
	@Test
	public void getTimesheetTest() throws Exception
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
		
		expect(mockAcmTimesheetDao.find(1L)).andReturn(timesheet);
		
		replayAll();
		
		AcmTimesheet found = timesheetService.get(1L);
		
		verifyAll();

		assertEquals(timesheet.getId(), found.getId());
	}
	
	@Test
	public void getObjectsFromSolrTest() throws Exception
	{
		String expected = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("txt/expectedSolrResponse.txt"));
		
		String objectType = "TIMESHEET";
		String solrQuery = "object_type_s:" + objectType + " AND -status_s:DELETE";
		
		expect(mockExecuteSolrQuery.getResultsByPredefinedQuery(mockAuthentication, SolrCore.QUICK_SEARCH, solrQuery, 0, 10, "")).andReturn(expected);
		
		replayAll();
		
		String response = timesheetService.getObjectsFromSolr(objectType, mockAuthentication, 0, 10, "");
		
		verifyAll();
		
		LOG.info("Results: " + response);
		
		int numFound = searchResults.getNumFound(response);
		JSONArray docs = searchResults.getDocuments(response);
		JSONObject doc = docs.getJSONObject(0);
		
		assertEquals(1, numFound);
		assertEquals("0001-TIMESHEET", doc.getString("id"));
	}
	
	@Test
	public void getTimesheetsByObjectIdTest() throws Exception
	{		
		long objectId = 5L;
		
		AcmTimesheet timesheet = new AcmTimesheet();
		timesheet.setId(1L);
		timesheet.setStatus("status");
		timesheet.setDetails("details");
		
		// We have three times in the provided timesheet. Two of them is for objectId 5L and one is for objectId 6L
		// At the end, the time with objectId 6L should be excluded and sum of the times should be only time1+time3
		// because we are going to search times only for objectId 5L
		AcmTime time1 = new AcmTime();
		time1.setId(3L);
		time1.setObjectId(5L);
		time1.setTimesheet(timesheet);
		time1.setCode("code1");
		time1.setType("type1");
		time1.setValue(8.0);
		
		AcmTime time2 = new AcmTime();
		time2.setId(4L);
		time2.setObjectId(6L);
		time2.setTimesheet(timesheet);
		time2.setCode("code2");
		time2.setType("type2");
		time2.setValue(7.0);
		
		AcmTime time3 = new AcmTime();
		time3.setId(9L);
		time3.setObjectId(5L);
		time3.setTimesheet(timesheet);
		time3.setCode("code3");
		time3.setType("type3");
		time3.setValue(8.0);
		
		timesheet.setTimes(Arrays.asList(time1, time2, time3));
		
		expect(mockAcmTimesheetDao.findByObjectId(objectId)).andReturn(Arrays.asList(timesheet));
		
		replayAll();
		
		List<AcmTimesheet> found = timesheetService.getByObjectId(objectId);
		
		verifyAll();

		double totalHours = 0.0;
		for (AcmTime time : found.get(0).getTimes())
		{
			totalHours += time.getValue();
		}
		
		assertEquals(2, found.get(0).getTimes().size());
		assertEquals(16.0, totalHours, 1);
	}	
}
