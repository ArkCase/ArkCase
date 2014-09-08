package com.armedia.acm.pentaho.config;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ReportNameEnumTest {
	private String COMPLAINT_REPORT = "Complaint Report";
	    
	@Test
	public void test() {
		ReportName reportEnumName = ReportName.fromString(COMPLAINT_REPORT);
        assertEquals(ReportName.COMPLAINT_REPORT, reportEnumName);
	}

}
