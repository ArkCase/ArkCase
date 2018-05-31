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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PentahoReportUrlTest
{
    private Map<String, Object> reportsProperties;
    private Properties reportServerConfigurationProperties;
    private String PENTAHO_SERVER_URL = "http://localhost";
    private String PENTAHO_SERVER_PORT = "8080";
    private String COMPLAINT_REPORT = "/pentaho/api/repos/:public:opm-ecms:ComplaintReport.prpt/viewer";
    private String BILLING_REPORT = "/pentaho/api/repos/:public:opm-ecms:Billing.prpt/viewer";
    private PentahoReportUrl reportUrl;

    @Before
    public void setUp() throws Exception
    {
        reportsProperties = new HashMap<>();
        reportServerConfigurationProperties = new Properties();

        reportServerConfigurationProperties.put("PENTAHO_SERVER_URL", PENTAHO_SERVER_URL);
        reportServerConfigurationProperties.put("PENTAHO_SERVER_PORT", PENTAHO_SERVER_PORT);
        reportsProperties.put("COMPLAINT_REPORT", COMPLAINT_REPORT);
        reportsProperties.put("BILLING_REPORT", BILLING_REPORT);

        reportUrl = new PentahoReportUrl();
        reportUrl.setReportsProperties(reportsProperties);
        reportUrl.setReportServerConfigurationProperties(reportServerConfigurationProperties);
    }

    @Test
    public void getNewReportUrlList() throws Exception
    {
        Map<String, String> urlMap = reportUrl.getNewReportUrlList();
        assertTrue(urlMap.containsKey(ReportName.COMPLAINT_REPORT.getDisplayName()));
        assertEquals(PENTAHO_SERVER_URL + ":" + PENTAHO_SERVER_PORT + COMPLAINT_REPORT,
                urlMap.get(ReportName.COMPLAINT_REPORT.getDisplayName()));
        assertTrue(urlMap.containsKey(ReportName.BILLING_REPORT.getDisplayName()));
        assertEquals(PENTAHO_SERVER_URL + ":" + PENTAHO_SERVER_PORT + BILLING_REPORT,
                urlMap.get(ReportName.BILLING_REPORT.getDisplayName()));
    }

    @Test
    public void getReportUrlPathTest() throws Exception
    {
        String path = reportUrl.getNewReportUrl(ReportName.COMPLAINT_REPORT.name());
        assertEquals(PENTAHO_SERVER_URL + ":" + PENTAHO_SERVER_PORT + COMPLAINT_REPORT, path);
    }
}
