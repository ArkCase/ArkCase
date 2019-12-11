package com.armedia.acm.pentaho.config;

/*-
 * #%L
 * Tool Integrations: report Configuration
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class PentahoReportUrlTest extends EasyMockSupport
{
    private Map<String, String> reportsProperties;
    private String PENTAHO_SERVER_URL = "http://localhost";
    private int PENTAHO_SERVER_PORT = 8080;
    private String COMPLAINT_REPORT = "/pentaho/api/repos/:public:opm-ecms:ComplaintReport.prpt/viewer";
    private String BILLING_REPORT = "/pentaho/api/repos/:public:opm-ecms:Billing.prpt/viewer";
    private PentahoReportUrl reportUrl;
    private PentahoReportsConfig reportsConfig;

    @Before
    public void setUp()
    {
        reportsProperties = new HashMap<>();
        reportsProperties.put("COMPLAINT_REPORT", COMPLAINT_REPORT);
        reportsProperties.put("BILLING_REPORT", BILLING_REPORT);

        reportUrl = new PentahoReportUrl();

        reportsConfig = createMock(PentahoReportsConfig.class);
        reportUrl.setReportsConfig(reportsConfig);
    }

    @Test
    public void getNewReportUrlList()
    {
        expect(reportsConfig.getServerUrl()).andReturn(PENTAHO_SERVER_URL);
        expect(reportsConfig.getServerPort()).andReturn(PENTAHO_SERVER_PORT);
        expect(reportsConfig.getReports()).andReturn(reportsProperties);

        replayAll();
        Map<String, String> urlMap = reportUrl.getNewReportUrlList();
        verifyAll();

        assertTrue(urlMap.containsKey(ReportName.COMPLAINT_REPORT.getDisplayName()));
        assertEquals(PENTAHO_SERVER_URL + ":" + PENTAHO_SERVER_PORT + COMPLAINT_REPORT,
                urlMap.get(ReportName.COMPLAINT_REPORT.getDisplayName()));
        assertTrue(urlMap.containsKey(ReportName.BILLING_REPORT.getDisplayName()));
        assertEquals(PENTAHO_SERVER_URL + ":" + PENTAHO_SERVER_PORT + BILLING_REPORT,
                urlMap.get(ReportName.BILLING_REPORT.getDisplayName()));
    }

    @Test
    public void getReportUrlPathTest()
    {
        expect(reportsConfig.getServerUrl()).andReturn(PENTAHO_SERVER_URL);
        expect(reportsConfig.getServerPort()).andReturn(PENTAHO_SERVER_PORT);
        expect(reportsConfig.getReports()).andReturn(reportsProperties);

        replayAll();
        String path = reportUrl.getNewReportUrl(ReportName.COMPLAINT_REPORT.name());
        verifyAll();

        assertEquals(PENTAHO_SERVER_URL + ":" + PENTAHO_SERVER_PORT + COMPLAINT_REPORT, path);
    }
}
