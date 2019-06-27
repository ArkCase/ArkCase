package com.armedia.acm.services.dataupdate.web;

/*-
 * #%L
 * ACM Service: Data Update Service
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
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;

import com.armedia.acm.quartz.scheduler.AcmSchedulerService;
import com.armedia.acm.services.dataupdate.service.SolrReindexService;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.quartz.JobDataMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SolrReindexServiceTests extends EasyMockSupport
{
    private List<Class> solrList;
    private Map<String, String> solrMap;
    private SolrReindexService solrReindexService;
    private AcmSchedulerService mockSchedulerService;

    @Before
    public void setUp()
    {
        solrReindexService = new SolrReindexService();
        mockSchedulerService = createMock(AcmSchedulerService.class);
        solrReindexService.setSchedulerService(mockSchedulerService);

        solrList = Arrays.asList(AcmUser.class, AcmGroup.class);

        solrMap = new HashMap<>();
        solrMap.put(AcmUser.class.getName(), "testValueUser");
        solrMap.put(AcmGroup.class.getName(), "testValueGroup");
    }

    @Test
    public void validateRemovedEntities()
    {
        JobDataMap jobDataMap = new JobDataMap(solrMap);
        expect(mockSchedulerService.getJobDataMap("jpaBatchUpdateJob")).andReturn(jobDataMap);
        mockSchedulerService.triggerJob("jpaBatchUpdateJob", jobDataMap);
        expectLastCall().once();

        replayAll();

        solrReindexService.reindex(solrList);

        verifyAll();

        assertEquals(0, jobDataMap.size());
    }

}
