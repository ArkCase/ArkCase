/**
 * 
 */
package com.armedia.acm.services.costsheet.service;

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

import com.armedia.acm.services.costsheet.dao.AcmCostsheetDao;
import com.armedia.acm.services.costsheet.model.AcmCost;
import com.armedia.acm.services.costsheet.model.AcmCostsheet;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchResults;

/**
 * @author riste.tutureski
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-library-service-costsheet-test.xml"
})
public class CostsheetServiceTest extends EasyMockSupport {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	
	private CostsheetServiceImpl costsheetService;
	private SearchResults searchResults;
	private Authentication mockAuthentication;
	private AcmCostsheetDao mockAcmCostsheetDao;
	private ExecuteSolrQuery mockExecuteSolrQuery;
	private Map<String, String> submissionStatusesMap;
	
	@Before
    public void setUp() throws Exception
    {
		costsheetService = new CostsheetServiceImpl();
		searchResults = new SearchResults();
		mockAuthentication = createMock(Authentication.class);
		mockAcmCostsheetDao = createMock(AcmCostsheetDao.class);
		mockExecuteSolrQuery = createMock(ExecuteSolrQuery.class);
		
		submissionStatusesMap = new HashMap<String, String>();
		submissionStatusesMap.put("Save", "DRAFT");
		submissionStatusesMap.put("Submit", "IN_APPROVAL");
		
		costsheetService.setAcmCostsheetDao(mockAcmCostsheetDao);
		costsheetService.setExecuteSolrQuery(mockExecuteSolrQuery);
		costsheetService.setSubmissionStatusesMap(submissionStatusesMap);		
    }
	
	
	@Test
	public void saveCostsheetTest() throws Exception
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
		
		Capture<AcmCostsheet> costsheetCapture = new Capture<AcmCostsheet>();
		
		expect(mockAcmCostsheetDao.save(capture(costsheetCapture))).andReturn(costsheet);
		
		replayAll();
		
		AcmCostsheet saved = costsheetService.save(costsheet);
		
		verifyAll();

		assertEquals(saved.getId(), costsheetCapture.getValue().getId());
	}
	
	@Test
	public void saveCostsheetAsDraftTest() throws Exception
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
		
		Capture<AcmCostsheet> costsheetCapture = new Capture<AcmCostsheet>();
		
		expect(mockAcmCostsheetDao.save(capture(costsheetCapture))).andReturn(costsheet);
		
		replayAll();
		
		AcmCostsheet saved = costsheetService.save(costsheet, "Save");
		
		verifyAll();

		assertEquals(costsheetCapture.getValue().getId(), saved.getId());
		assertEquals("DRAFT", saved.getStatus());
	}
	
	@Test
	public void saveCostsheetAsInApprovalTest() throws Exception
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
		
		Capture<AcmCostsheet> costsheetCapture = new Capture<AcmCostsheet>();
		
		expect(mockAcmCostsheetDao.save(capture(costsheetCapture))).andReturn(costsheet);
		
		replayAll();
		
		AcmCostsheet saved = costsheetService.save(costsheet, "Submit");
		
		verifyAll();

		assertEquals(costsheetCapture.getValue().getId(), saved.getId());
		assertEquals("IN_APPROVAL", saved.getStatus());
	}
	
	@Test
	public void getCostsheetTest() throws Exception
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
		
		expect(mockAcmCostsheetDao.find(1L)).andReturn(costsheet);
		
		replayAll();
		
		AcmCostsheet found = costsheetService.get(1L);
		
		verifyAll();

		assertEquals(costsheet.getId(), found.getId());
	}
	
	@Test
	public void getObjectsFromSolrTest() throws Exception
	{
		String expected = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("txt/expectedSolrResponse.txt"));
		
		String objectType = "COSTSHEET";
		String solrQuery = "object_type_s:" + objectType + " AND -status_s:DELETE";
		
		expect(mockExecuteSolrQuery.getResultsByPredefinedQuery(mockAuthentication, SolrCore.QUICK_SEARCH, solrQuery, 0, 10, "")).andReturn(expected);
		
		replayAll();
		
		String response = costsheetService.getObjectsFromSolr(objectType, mockAuthentication, 0, 10, "", null);
		
		verifyAll();
		
		LOG.info("Results: " + response);
		
		int numFound = searchResults.getNumFound(response);
		JSONArray docs = searchResults.getDocuments(response);
		JSONObject doc = docs.getJSONObject(0);
		
		assertEquals(1, numFound);
		assertEquals("0001-COSTSHEET", doc.getString("id"));
	}
	
	@Test
	public void getCostsheetsByObjectIdTest() throws Exception
	{		
		long objectId = 5L;
		String objectType = "type";
		
		// We have three costsheets. Two of them is for objectId 5L and one is for objectId 6L
		// At the end, the costsheet with objectId 6L should be excluded
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
		
		expect(mockAcmCostsheetDao.findByObjectIdAndType(objectId, objectType, 0, 10, "")).andReturn(Arrays.asList(costsheet1, costsheet3));
		
		replayAll();
		
		List<AcmCostsheet> found = costsheetService.getByObjectIdAndType(objectId, objectType, 0, 10, "");
		
		verifyAll();
		
		assertEquals(2, found.size());
	}	
}
