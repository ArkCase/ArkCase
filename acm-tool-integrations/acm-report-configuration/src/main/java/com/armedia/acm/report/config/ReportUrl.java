package com.armedia.acm.report.config;

import java.util.Map;

public interface ReportUrl {
	/**
	 * Retrieve the form server url based on the report name.
	 * 
	 * @param formName
	 * @return
	 */
	public String getNewReportUrl(String reportName);

	/**
	 * This method get all the report urls in the properties file and
	 * return a sorted map.
	 * 
	 * @return
	 */
	public Map<String, String> getNewReportUrlList();

}
