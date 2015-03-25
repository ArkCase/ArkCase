package com.armedia.acm.pentaho.config;

import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.armedia.acm.report.config.ReportUrl;

public class PentahoReportUrl implements ReportUrl{
    private Logger log = LoggerFactory.getLogger(getClass());
    private static final String REPORT_SERVER_URL = "PENTAHO_SERVER_URL";
    private static final String REPORT_SERVER_PORT = "PENTAHO_SERVER_PORT";
    private static final String PENTAHO_SERVER_INTERNAL_URL = "PENTAHO_SERVER_INTERNAL_URL";
    private static final String PENTAHO_SERVER_INTERNAL_PORT = "PENTAHO_SERVER_INTERNAL_PORT";
    private static final String PENTAHO_SERVER_USER = "PENTAHO_SERVER_USER";
    private static final String PENTAHO_SERVER_PASSWORD = "PENTAHO_SERVER_PASSWORD";
    private static final String PENTAHO_REPORT_URL_TEMPLATE = "PENTAHO_REPORT_URL_TEMPLATE";
    private static final String REPORTS_URL = "PENTAHO_REPORTS_URL";

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
	
	/**
	 * This method get all the report urls in the properties file and
	 * return a sorted map.
	 * 
	 * @return
	 */
	@Override
	public Map<String, String> getNewReportUrlList() {
        Map<String,String> urlsMap = new TreeMap<String, String>();
        String serverFormUrl = getReportsProperties().get(REPORT_SERVER_URL).toString();
        String serverFormPort = getReportsProperties().get(REPORT_SERVER_PORT).toString();

        for(Map.Entry<String, Object> entry : getReportsProperties().entrySet()){
            String keyStr = entry.getKey();
            
            //skip the host and port properties
            if ( !keyStr.equalsIgnoreCase(REPORT_SERVER_URL) && !keyStr.equalsIgnoreCase(REPORT_SERVER_PORT) &&
            	 !keyStr.equalsIgnoreCase(PENTAHO_SERVER_USER) && !keyStr.equalsIgnoreCase(PENTAHO_SERVER_PASSWORD) &&
            	 !keyStr.equalsIgnoreCase(REPORTS_URL) && !keyStr.equalsIgnoreCase(PENTAHO_REPORT_URL_TEMPLATE) &&
            	 !keyStr.equalsIgnoreCase(PENTAHO_SERVER_INTERNAL_URL) && !keyStr.equalsIgnoreCase(PENTAHO_SERVER_INTERNAL_PORT)) {
            	ReportName enumName = ReportName.valueOf(keyStr);
            	if (null != enumName) {
                	urlsMap.put(enumName.getDisplayName(), formulateUrl(serverFormUrl, serverFormPort, entry.getValue()));            		
            	}
            }
            
        }
        
		return urlsMap;
	}
	
	private String formulateUrl(String host, String port, Object path) {
        StringBuilder builder = new StringBuilder();
        builder.append(host);
        builder.append(port);
    	builder.append(path);
		return builder.toString();
	}
	
	public String getReportsUrl()
	{
		StringBuilder builder = new StringBuilder();
		
        String serverFormUrl = getReportsProperties().get(PENTAHO_SERVER_INTERNAL_URL).toString();

        builder.append(serverFormUrl);

        String serverFormPort = getReportsProperties().get(PENTAHO_SERVER_INTERNAL_PORT).toString();
        builder.append(serverFormPort);
        
        String pentahoReportsUrl = getReportsProperties().get(REPORTS_URL).toString();
        builder.append(pentahoReportsUrl);
        
        String url = builder.toString();
        log.debug("getReportsUrl(): " + url);
        
        return url;
	}

}
