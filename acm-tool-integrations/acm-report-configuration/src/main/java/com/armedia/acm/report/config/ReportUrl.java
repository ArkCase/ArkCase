package com.armedia.acm.report.config;

public interface ReportUrl {
	/**
	 * Retrieve the form server url based on the form name.
	 * 
	 * @param formName
	 * @return
	 */
	public String getNewReportUrl(String reportName);
	
}
