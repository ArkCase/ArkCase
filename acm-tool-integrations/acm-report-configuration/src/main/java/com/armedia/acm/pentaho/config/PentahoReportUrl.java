package com.armedia.acm.pentaho.config;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.armedia.acm.report.config.ReportUrl;

public class PentahoReportUrl implements ReportUrl{
    private Logger log = LoggerFactory.getLogger(getClass());
    private static final String REPORT_SERVER_URL = "PENTAHO_SERVER_URL";
    private static final String REPORT_SERVER_PORT = "PENTAHO_SERVER_PORT";

    /**
     * List of form-specific properties.
     */
    private Map<String, Object> reportsProperties;
    
	public Map<String, Object> getReportsProperties() {
		return reportsProperties;
	}

	public void setReportsProperties(Map<String, Object> reportProperties) {
		this.reportsProperties = reportProperties;
	}

	@Override
	public String getNewReportUrl(String reportName) {
		return getReportUrlPath(reportName);
	}

	public String getReportURL() {
        //form url data
        StringBuilder builder = new StringBuilder();
        String serverFormUrl = getReportsProperties().get(REPORT_SERVER_URL).toString();
        builder.append(serverFormUrl);
        String serverFormPort = getReportsProperties().get(REPORT_SERVER_PORT).toString();
        builder.append(serverFormPort);
        String url = builder.toString();
        log.debug("getReportURL(): " + url);
        return url;
	}

	/**
	 * Url path for create a new complaint report form in Pentaho.
	 * 
	 * @return
	 */
	public String getReportUrlPath(String reportName) {
        StringBuilder builder = new StringBuilder();
        String serverFormUrl = getReportsProperties().get(REPORT_SERVER_URL).toString();
        builder.append(serverFormUrl);
        String serverFormPort = getReportsProperties().get(REPORT_SERVER_PORT).toString();
        builder.append(serverFormPort);
		String pathStr = getReportsProperties().get(reportName).toString();
        builder.append(pathStr);
		String path = builder.toString();
        log.debug("getReportUrlPath(): " + path);
		return path;
	}

}
