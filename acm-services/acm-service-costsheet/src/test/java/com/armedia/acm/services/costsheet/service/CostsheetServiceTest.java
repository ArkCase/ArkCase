/**
 *
 */
package com.armedia.acm.services.costsheet.service;

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

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import com.armedia.acm.services.costsheet.dao.AcmCostsheetDao;
import com.armedia.acm.services.costsheet.model.AcmCost;
import com.armedia.acm.services.costsheet.model.AcmCostsheet;
import com.armedia.acm.services.costsheet.pipeline.CostsheetPipelineContext;
import com.armedia.acm.services.pipeline.PipelineManager;
import com.armedia.acm.services.search.model.solr.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchResults;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.easymock.Capture;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author riste.tutureski
 */
public class CostsheetServiceTest 
{

    private Logger LOG = LogManager.getLogger(getClass());

    private CostsheetServiceImpl costsheetService;
    private SearchResults searchResults;
    private Authentication mockAuthentication;
    private AcmCostsheetDao mockAcmCostsheetDao;
    private ExecuteSolrQuery mockExecuteSolrQuery;
    private Map<String, String> submissionStatusesMap;
    private PipelineManager<AcmCostsheet, CostsheetPipelineContext> pipelineManager;

    private Object[] mocks;

    @Before
    public void setUp() throws Exception
    {
        costsheetService = new CostsheetServiceImpl();
        searchResults = new SearchResults();
        mockAuthentication = createMock(Authentication.class);
        mockAcmCostsheetDao = createMock(AcmCostsheetDao.class);
        mockExecuteSolrQuery = createMock(ExecuteSolrQuery.class);
        pipelineManager = createMock(PipelineManager.class);

        submissionStatusesMap = new HashMap<>();
        submissionStatusesMap.put("Save", "DRAFT");
        submissionStatusesMap.put("Submit", "IN_APPROVAL");

        costsheetService.setAcmCostsheetDao(mockAcmCostsheetDao);
        costsheetService.setExecuteSolrQuery(mockExecuteSolrQuery);
        costsheetService.setSubmissionStatusesMap(submissionStatusesMap);
        costsheetService.setPipelineManager(pipelineManager);

        mocks = new Object[] { mockAuthentication, mockAcmCostsheetDao, mockExecuteSolrQuery, pipelineManager };

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

        Capture<AcmCostsheet> costsheetCapture = Capture.newInstance();

        expect(mockAcmCostsheetDao.save(capture(costsheetCapture))).andReturn(costsheet);
        expect(pipelineManager.executeOperation(anyObject(AcmCostsheet.class), anyObject(CostsheetPipelineContext.class),
                anyObject(PipelineManager.PipelineManagerOperation.class)))
                        .andAnswer(() -> mockAcmCostsheetDao.save(costsheet));

        replay(mocks);

        AcmCostsheet saved = costsheetService.save(costsheet, mockAuthentication, "Save");

        verify(mocks);

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

        Capture<AcmCostsheet> costsheetCapture = Capture.newInstance();

        expect(mockAcmCostsheetDao.save(capture(costsheetCapture))).andReturn(costsheet);
        expect(pipelineManager.executeOperation(anyObject(AcmCostsheet.class), anyObject(CostsheetPipelineContext.class),
                anyObject(PipelineManager.PipelineManagerOperation.class)))
                        .andAnswer(() -> mockAcmCostsheetDao.save(costsheet));

        replay(mocks);

        AcmCostsheet saved = costsheetService.save(costsheet, "Save");

        verify(mocks);

        assertEquals(costsheetCapture.getValue().getId(), saved.getId());
        // our responsiblity now is only to call the pipeline save... so we don't have to check any effects of the
        // rules.
        // Sometime we need separate unit tests on the rules themselves.
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

        Capture<AcmCostsheet> costsheetCapture = Capture.newInstance();

        expect(mockAcmCostsheetDao.save(capture(costsheetCapture))).andReturn(costsheet);
        expect(pipelineManager.executeOperation(anyObject(AcmCostsheet.class), anyObject(CostsheetPipelineContext.class),
                anyObject(PipelineManager.PipelineManagerOperation.class)))
                        .andAnswer(() -> mockAcmCostsheetDao.save(costsheet));

        replay(mocks);

        AcmCostsheet saved = costsheetService.save(costsheet, "Submit");

        verify(mocks);

        assertEquals(costsheetCapture.getValue().getId(), saved.getId());
        // our responsiblity now is only to call the pipeline save... so we don't have to check any effects of the
        // rules.
        // Sometime we need separate unit tests on the rules themselves.
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

        replay(mocks);

        AcmCostsheet found = costsheetService.get(1L);

        verify(mocks);

        assertEquals(costsheet.getId(), found.getId());
    }

    @Test
    public void getObjectsFromSolrWithQueryStringTest() throws Exception
    {
        String expected = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("txt/expectedSolrResponse.txt"));

        String objectType = "COSTSHEET";
        String searchQuery = "*";
        String solrQuery = "object_type_s:" + objectType + " AND name:" + searchQuery + " AND -status_lcs:DELETE";

        expect(mockExecuteSolrQuery.getResultsByPredefinedQuery(mockAuthentication, SolrCore.ADVANCED_SEARCH, solrQuery, 0, 10, ""))
                .andReturn(expected);

        replay(mocks);

        String response = costsheetService.getObjectsFromSolr(objectType, mockAuthentication, 0, 10, "", "*", null);

        verify(mocks);

        LOG.info("Results: " + response);

        int numFound = searchResults.getNumFound(response);
        JSONArray docs = searchResults.getDocuments(response);
        JSONObject doc = docs.getJSONObject(0);

        assertEquals(1, numFound);
        assertEquals("0001-COSTSHEET", doc.getString("id"));
    }

    @Test
    public void getObjectsFromSolrTest() throws Exception
    {
        String expected = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("txt/expectedSolrResponse.txt"));

        String objectType = "COSTSHEET";
        String solrQuery = "object_type_s:" + objectType + " AND -status_lcs:DELETE";

        expect(mockExecuteSolrQuery.getResultsByPredefinedQuery(mockAuthentication, SolrCore.ADVANCED_SEARCH, solrQuery, 0, 10, ""))
                .andReturn(expected);

        replay(mocks);

        String response = costsheetService.getObjectsFromSolr(objectType, mockAuthentication, 0, 10, "", null);

        verify(mocks);

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
        Long objectId = 5L;
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

        replay(mocks);

        List<AcmCostsheet> found = costsheetService.getByObjectIdAndType(objectId, objectType, 0, 10, "");

        verify(mocks);

        assertEquals(2, found.size());
    }
}
