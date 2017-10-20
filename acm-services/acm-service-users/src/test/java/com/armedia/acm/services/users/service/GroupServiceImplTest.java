package com.armedia.acm.services.users.service;

import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.users.service.group.GroupServiceImpl;
import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.mule.api.MuleException;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static junit.framework.Assert.assertEquals;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;

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
    public void buildQueryForGroupWithoutSpaceAndNoStatusFilterTest() throws MuleException
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
    public void buildQueryForGroupWithoutSpaceAndValidStatusFilterTest() throws MuleException
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
    public void buildQueryForGroupWithSpaceAndValidStatusFilterTest() throws MuleException
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
