/**
 *
 */
package com.armedi.acm.services.timesheet.service;

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

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import com.armedia.acm.services.pipeline.PipelineManager;
import com.armedia.acm.services.search.model.solr.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchResults;
import com.armedia.acm.services.timesheet.dao.AcmTimesheetDao;
import com.armedia.acm.services.timesheet.model.AcmTime;
import com.armedia.acm.services.timesheet.model.AcmTimesheet;
import com.armedia.acm.services.timesheet.pipeline.TimesheetPipelineContext;
import com.armedia.acm.services.timesheet.service.TimesheetServiceImpl;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author riste.tutureski
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-library-service-timesheet-test.xml"
})
public class TimesheetServiceTest extends EasyMockSupport
{

    private Logger LOG = LogManager.getLogger(getClass());

    private TimesheetServiceImpl timesheetService;
    private SearchResults searchResults;
    private Authentication mockAuthentication;
    private AcmTimesheetDao mockAcmTimesheetDao;
    private ExecuteSolrQuery mockExecuteSolrQuery;
    private PipelineManager<AcmTimesheet, TimesheetPipelineContext> pipelineManager;
    private Map<String, String> submissionStatusesMap;

    @Before
    public void setUp() throws Exception
    {
        timesheetService = new TimesheetServiceImpl();
        searchResults = new SearchResults();
        mockAuthentication = createMock(Authentication.class);
        mockAcmTimesheetDao = createMock(AcmTimesheetDao.class);
        mockExecuteSolrQuery = createMock(ExecuteSolrQuery.class);
        pipelineManager = createMock(PipelineManager.class);

        submissionStatusesMap = new HashMap<>();
        submissionStatusesMap.put("Save", "DRAFT");
        submissionStatusesMap.put("Submit", "IN_APPROVAL");

        timesheetService.setAcmTimesheetDao(mockAcmTimesheetDao);
        timesheetService.setExecuteSolrQuery(mockExecuteSolrQuery);
        timesheetService.setSubmissionStatusesMap(submissionStatusesMap);
        timesheetService.setPipelineManager(pipelineManager);
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

        Capture<AcmTimesheet> timesheetCapture = EasyMock.newCapture();

        expect(mockAcmTimesheetDao.save(capture(timesheetCapture))).andReturn(timesheet);
        expect(pipelineManager.executeOperation(anyObject(AcmTimesheet.class), anyObject(TimesheetPipelineContext.class),
                anyObject(PipelineManager.PipelineManagerOperation.class))).andAnswer(() -> mockAcmTimesheetDao.save(timesheet));
        replayAll();

        AcmTimesheet saved = timesheetService.save(timesheet, mockAuthentication, "Save");

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

        Capture<AcmTimesheet> timesheetCapture = EasyMock.newCapture();

        expect(mockAcmTimesheetDao.save(capture(timesheetCapture))).andReturn(timesheet);
        expect(pipelineManager.executeOperation(anyObject(AcmTimesheet.class), anyObject(TimesheetPipelineContext.class),
                anyObject(PipelineManager.PipelineManagerOperation.class))).andAnswer(() -> mockAcmTimesheetDao.save(timesheet));
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

        Capture<AcmTimesheet> timesheetCapture = EasyMock.newCapture();

        expect(mockAcmTimesheetDao.save(capture(timesheetCapture))).andReturn(timesheet);
        expect(pipelineManager.executeOperation(anyObject(AcmTimesheet.class), anyObject(TimesheetPipelineContext.class),
                anyObject(PipelineManager.PipelineManagerOperation.class))).andAnswer(() -> mockAcmTimesheetDao.save(timesheet));
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
    public void getObjectsFromSolrTestWithQueryString() throws Exception
    {
        String expected = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("txt/expectedSolrResponse.txt"));

        String objectType = "TIMESHEET";
        String searchQuery = "*";
        String solrQuery = "object_type_s:" + objectType + " AND name:" + searchQuery + " AND -status_lcs:DELETE";

        expect(mockExecuteSolrQuery.getResultsByPredefinedQuery(mockAuthentication, SolrCore.ADVANCED_SEARCH, solrQuery, 0, 10, ""))
                .andReturn(expected);

        replayAll();

        String response = timesheetService.getObjectsFromSolr(objectType, mockAuthentication, 0, 10, "", "*", null);

        verifyAll();

        LOG.info("Results: " + response);

        int numFound = searchResults.getNumFound(response);
        JSONArray docs = searchResults.getDocuments(response);
        JSONObject doc = docs.getJSONObject(0);

        assertEquals(1, numFound);
        assertEquals("0001-TIMESHEET", doc.getString("id"));
    }

    @Test
    public void getObjectsFromSolrTest() throws Exception
    {
        String expected = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("txt/expectedSolrResponse.txt"));

        String objectType = "TIMESHEET";
        String searchQuery = "*";
        String solrQuery = "object_type_s:" + objectType + " AND -status_lcs:DELETE";

        expect(mockExecuteSolrQuery.getResultsByPredefinedQuery(mockAuthentication, SolrCore.ADVANCED_SEARCH, solrQuery, 0, 10, ""))
                .andReturn(expected);

        replayAll();

        String response = timesheetService.getObjectsFromSolr(objectType, mockAuthentication, 0, 10, "", null);

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
        String objectType = "type";

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

        expect(mockAcmTimesheetDao.findByObjectIdAndType(objectId, objectType, 0, 10, "")).andReturn(Arrays.asList(timesheet));

        replayAll();

        List<AcmTimesheet> found = timesheetService.getByObjectIdAndType(objectId, objectType, 0, 10, "");

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
