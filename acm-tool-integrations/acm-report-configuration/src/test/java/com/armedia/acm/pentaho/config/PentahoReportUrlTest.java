package com.armedia.acm.pentaho.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class PentahoReportUrlTest {
    private Map<String, Object> reportsProperties;
    private String PENTAHO_SERVER_URL="http://localhost:";
    private String PENTAHO_SERVER_PORT="8080";
    private String COMPLAINT_REPORT="/pentaho/api/repos/:public:opm-ecms:ComplaintReport.prpt/viewer";
    private String BILLING_REPORT="/pentaho/api/repos/:public:opm-ecms:Billing.prpt/viewer";
    private PentahoReportUrl reportUrl;
    
    @Before
    public void setUp() throws Exception
    {    	
    	reportsProperties = new HashMap<String, Object>();
    	reportsProperties.put("PENTAHO_SERVER_URL", PENTAHO_SERVER_URL);
    	reportsProperties.put("PENTAHO_SERVER_PORT", PENTAHO_SERVER_PORT);
    	reportsProperties.put("COMPLAINT_REPORT", COMPLAINT_REPORT);
    	reportsProperties.put("BILLING_REPORT", BILLING_REPORT);
    	reportUrl = new PentahoReportUrl();
    	reportUrl.setReportsProperties(reportsProperties);
    }

    @Test
    public void getNewReportUrlList() throws Exception
    {
    	Map<String, String> urlMap = reportUrl.getNewReportUrlList();
    	assertTrue(urlMap.containsKey(ReportName.COMPLAINT_REPORT.getDisplayName()));
        assertEquals(PENTAHO_SERVER_URL+PENTAHO_SERVER_PORT+COMPLAINT_REPORT, urlMap.get(ReportName.COMPLAINT_REPORT.getDisplayName()));
    	assertTrue(urlMap.containsKey(ReportName.BILLING_REPORT.getDisplayName()));
        assertEquals(PENTAHO_SERVER_URL+PENTAHO_SERVER_PORT+BILLING_REPORT, urlMap.get(ReportName.BILLING_REPORT.getDisplayName()));    	
    }
    
    @Test
    public void getReportUrlPathTest() throws Exception
    {
    	String path = reportUrl.getNewReportUrl(ReportName.COMPLAINT_REPORT.name());
        assertEquals(PENTAHO_SERVER_URL+PENTAHO_SERVER_PORT+COMPLAINT_REPORT, path);
    }
}
