package com.armedia.acm.services.users.service;

/*-
 * #%L
 * ACM Service: Users
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

import static junit.framework.Assert.assertEquals;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;

import com.armedia.acm.services.search.exception.SolrException;
import com.armedia.acm.services.search.model.solr.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.users.service.group.GroupServiceImpl;

import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;

import java.util.Optional;

public class GroupServiceImplTest extends EasyMockSupport
{
    private GroupServiceImpl unit;
    private Authentication mockAuthentication;
    private ExecuteSolrQuery mockSolrQueryService;

    @Before
    public void setup()
    {
        mockSolrQueryService = createMock(ExecuteSolrQuery.class);
        mockAuthentication = createMock(Authentication.class);

        unit = new GroupServiceImpl();
        unit.setExecuteSolrQuery(mockSolrQueryService);
    }

    @Test
    public void buildQueryForGroupWithoutSpaceAndNoStatusFilterTest() throws SolrException
    {
        String group = "ADMIN";
        String query = "object_type_s:USER AND groups_id_ss:ADMIN";

        Capture<String> queryCapture = Capture.newInstance();

        expect(mockSolrQueryService.getResultsByPredefinedQuery(eq(mockAuthentication), eq(SolrCore.ADVANCED_SEARCH),
                capture(queryCapture), eq(0), eq(1000), eq(""))).andReturn("");

        replayAll();

        unit.getUserMembersForGroup(group, Optional.empty(), mockAuthentication);

        verifyAll();

        verifyQuery(query, queryCapture);
    }

    @Test
    public void buildQueryForGroupWithoutSpaceAndValidStatusFilterTest() throws SolrException
    {
        String group = "ADMIN";
        String status = "VALID";
        String query = "object_type_s:USER AND groups_id_ss:ADMIN AND status_lcs:VALID";

        Capture<String> queryCapture = Capture.newInstance();

        expect(mockSolrQueryService.getResultsByPredefinedQuery(eq(mockAuthentication), eq(SolrCore.ADVANCED_SEARCH),
                capture(queryCapture), eq(0), eq(1000), eq(""))).andReturn("");

        replayAll();

        unit.getUserMembersForGroup(group, Optional.of(status), mockAuthentication);

        verifyAll();

        verifyQuery(query, queryCapture);
    }

    @Test
    public void buildQueryForGroupWithSpaceAndValidStatusFilterTest() throws SolrException
    {
        String group = "SUPER ADMIN";
        String status = "VALID";
        String query = "object_type_s:USER AND groups_id_ss:\"SUPER ADMIN\" AND status_lcs:VALID";

        Capture<String> queryCapture = Capture.newInstance();

        expect(mockSolrQueryService.getResultsByPredefinedQuery(eq(mockAuthentication), eq(SolrCore.ADVANCED_SEARCH),
                capture(queryCapture), eq(0), eq(1000), eq(""))).andReturn("");

        replayAll();

        unit.getUserMembersForGroup(group, Optional.of(status), mockAuthentication);

        verifyAll();

        verifyQuery(query, queryCapture);
    }

    public void verifyQuery(String expectedQuery, Capture<String> queryCapture)
    {
        String actualQuery = queryCapture.getValue();
        assertEquals(expectedQuery, actualQuery);
    }
}
